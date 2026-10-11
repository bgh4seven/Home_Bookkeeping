package ru.home_bookkeeping.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class YandexGptClient {
    private static final URI ENDPOINT = URI.create(
            "https://llm.api.cloud.yandex.net/foundationModels/v1/completion");
    private static final String MODEL_URI =
            "gpt://b1gqnrlugcnrb0vfa5qr/yandexgpt-lite/latest";

    private static final String SYSTEM_PROMPT = """
            Ты помощник по анализу личного бюджета. По статистике за три месяца
            дай короткую оценку бюджета и ровно три конкретных совета.
            Опирайся только на переданные данные; по возможности приводи суммы
            и изменения по категориям. Не выдумывай причины расходов.
            recordedBalance — только доходы минус записанные расходы, а не остаток на счёте.
            scheduledMonthlyCreditPayments — плановые платежи, не факт их оплаты.
            Если creditPaymentsIncludedInExpenses = unknown, нельзя утверждать,
            сколько осталось денег после кредитов: возможен двойной учёт.
            activeDepositPrincipal — сумма вкладов, не свободные деньги.
            Не предлагай конкретные инвестиционные продукты.
            Пиши по-русски, кратко, без Markdown и нумерации внутри советов.
            Верни JSON строго по заданной схеме.
            """;

    private final HttpClient httpClient;
    private final String apiKey;

    public YandexGptClient() {
        String value = System.getenv("YANDEX_API_KEY");
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Не задана переменная окружения YANDEX_API_KEY");
        }
        apiKey = value.trim();
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public AiAnalysisResult analyze(JsonObject financialReport)
            throws IOException, InterruptedException {
        Objects.requireNonNull(financialReport, "financialReport");

        HttpRequest request = HttpRequest.newBuilder(ENDPOINT)
                .timeout(Duration.ofSeconds(180))
                .header("Authorization", "Api-Key " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        buildRequest(financialReport).toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("YandexGPT вернул HTTP " + response.statusCode()
                    + ": " + abbreviate(response.body(), 400));
        }

        try {
            JsonElement parsed = JsonParser.parseString(response.body());
            if (!parsed.isJsonObject()) {
                throw new IOException("Ответ API не является JSON-объектом");
            }
            JsonObject result = requireObject(parsed.getAsJsonObject(), "result");
            JsonArray alternatives = requireArray(result, "alternatives");
            if (alternatives.isEmpty() || !alternatives.get(0).isJsonObject()) {
                throw new IOException("В ответе API нет альтернативы генерации");
            }
            JsonObject alternative = alternatives.get(0).getAsJsonObject();
            String status = requireString(alternative, "status");
            if (!"ALTERNATIVE_STATUS_FINAL".equals(status)) {
                throw new IOException("Ответ модели не завершён: " + status);
            }

            String generated = requireString(
                    requireObject(alternative, "message"), "text");
            JsonElement modelJson = JsonParser.parseString(generated);
            if (!modelJson.isJsonObject()) {
                throw new IOException("Модель вернула не JSON-объект");
            }
            JsonObject analysis = modelJson.getAsJsonObject();
            String summary = requireString(analysis, "summary").trim();
            JsonArray adviceJson = requireArray(analysis, "advice");
            if (summary.isBlank() || adviceJson.size() != 3) {
                throw new IOException("ИИ вернул пустой обзор или не три рекомендации");
            }

            List<String> advice = new ArrayList<>(3);
            for (JsonElement item : adviceJson) {
                if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                    throw new IOException("Рекомендация не является текстом");
                }
                String text = item.getAsString().trim();
                if (text.isEmpty()) {
                    throw new IOException("ИИ вернул пустую рекомендацию");
                }
                advice.add(text);
            }

            JsonObject usage = result.has("usage") && result.get("usage").isJsonObject()
                    ? result.getAsJsonObject("usage") : new JsonObject();
            return new AiAnalysisResult(
                    summary,
                    List.copyOf(advice),
                    optionalInt(usage, "inputTextTokens"),
                    optionalInt(usage, "completionTokens"),
                    optionalInt(usage, "totalTokens"));
        } catch (JsonParseException | IllegalStateException e) {
            throw new IOException("Не удалось разобрать JSON от YandexGPT", e);
        }
    }

    private static JsonObject buildRequest(JsonObject report) {
        JsonObject options = new JsonObject();
        options.addProperty("stream", false);
        options.addProperty("temperature", 0.2);
        options.addProperty("maxTokens", "600");

        JsonObject summarySchema = new JsonObject();
        summarySchema.addProperty("type", "string");
        JsonObject adviceItemSchema = new JsonObject();
        adviceItemSchema.addProperty("type", "string");
        JsonObject adviceSchema = new JsonObject();
        adviceSchema.addProperty("type", "array");
        adviceSchema.add("items", adviceItemSchema);
        adviceSchema.addProperty("minItems", 3);
        adviceSchema.addProperty("maxItems", 3);
        JsonObject properties = new JsonObject();
        properties.add("summary", summarySchema);
        properties.add("advice", adviceSchema);
        JsonArray required = new JsonArray();
        required.add("summary");
        required.add("advice");
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        schema.add("properties", properties);
        schema.add("required", required);
        schema.addProperty("additionalProperties", false);
        JsonObject jsonSchema = new JsonObject();
        jsonSchema.add("schema", schema);

        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("text", SYSTEM_PROMPT);
        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("text", report.toString());
        JsonArray messages = new JsonArray();
        messages.add(system);
        messages.add(user);

        JsonObject request = new JsonObject();
        request.addProperty("modelUri", MODEL_URI);
        request.add("completionOptions", options);
        request.add("jsonSchema", jsonSchema);
        request.add("messages", messages);
        return request;
    }

    private static JsonObject requireObject(JsonObject parent, String field) throws IOException {
        JsonElement value = parent.get(field);
        if (value == null || !value.isJsonObject()) {
            throw new IOException("Отсутствует объект: " + field);
        }
        return value.getAsJsonObject();
    }

    private static JsonArray requireArray(JsonObject parent, String field) throws IOException {
        JsonElement value = parent.get(field);
        if (value == null || !value.isJsonArray()) {
            throw new IOException("Отсутствует массив: " + field);
        }
        return value.getAsJsonArray();
    }

    private static String requireString(JsonObject parent, String field) throws IOException {
        JsonElement value = parent.get(field);
        if (value == null || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IOException("Отсутствует текстовое поле: " + field);
        }
        return value.getAsString();
    }

    private static int optionalInt(JsonObject object, String field) {
        try {
            JsonElement value = object.get(field);
            return value == null || value.isJsonNull() ? -1
                    : Integer.parseInt(value.getAsString());
        } catch (NumberFormatException | IllegalStateException e) {
            return -1;
        }
    }

    private static String abbreviate(String value, int maxChars) {
        if (value == null) return "";
        return value.length() <= maxChars ? value : value.substring(0, maxChars) + "...";
    }

    public record AiAnalysisResult(
            String summary,
            List<String> advice,
            int inputTokens,
            int completionTokens,
            int totalTokens) {}
}
