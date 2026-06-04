package ru.home_bookkeeping.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.ls.LSOutput;
import ru.home_bookkeeping.backend.model.Deposit;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс DataBase — хранилище вкладов.
 * Отвечает за запись и чтение объектов Deposit.
 */
public class DataBase {
    // Ключ — порядковый номер вклада, значение — объект Deposit
    private Map<Integer, Deposit> deposits;
    //Путь к файлу базы данных
    private Path dbPath = Paths.get("src/main/resources/db_deposit.json");
    //Создание Gson для работы с db
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    //Инициализатор, запустится при создании экземпляра класса, подгрузит дб
    {
        deposits = readDB(dbPath);
    }

    /**
     * Создаёт новый объект Deposit и сохраняет его в Map.
     *
     * @param number   Порядковый номер вклада (ключ в Map)
     * @param bankName Наименование банка
     * @param amount   Сумма вклада (в рублях)
     * @param percent  Годовая процентная ставка
     * @param days     Срок вклада в днях
     * @param openDate Дата открытия вклада
     */
    public void addDeposit(int number, String bankName, double amount,
                           double percent, int days, LocalDate openDate) {
        Deposit deposit = new Deposit(number, bankName, amount, percent, days, openDate);
        deposits.put(number, deposit);
    }

    public void deleteDeposit(int number) {
        deposits.remove(number);
    }

    private Map<Integer, Deposit> readDB(Path path) {
        Map<Integer, Deposit> db = new HashMap<>();
        try (FileReader reader = new FileReader(path.toFile())) {
            Type type = new TypeToken<Map<Integer, Deposit>>() {}.getType();
            db = gson.fromJson(reader, type);
            if (db == null) {
                System.out.println("Empty DB");
                db = new HashMap<>();
            }
        } catch (FileNotFoundException e) {
            System.out.println("DataBase not found. A new one has been created.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return db;
    }

    public void writeDB() {
        try (FileWriter writer = new FileWriter(dbPath.toFile())) {
            String db = gson.toJson(deposits);
            writer.write(db);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Ошибка записи DB... " + e.getMessage());
            System.out.println("Изменения не были сохранены");
        }
    }

    /**
     * Возвращает объект Deposit из Map по порядковому номеру.
     * Если вклад с таким номером не найден — возвращает null.
     *
     * @param number Порядковый номер вклада
     * @return Deposit или null
     */
    public Deposit getDeposit(int number) {
        return deposits.get(number);
    }

    // Возвращает все вклады в виде коллекции (для таблицы)
    public List<Deposit> getAllDeposits() {
        return new ArrayList<>(deposits.values());
    }

    // Возвращает следующий свободный номер вклада
    public int getNextNumber() {
        if (deposits.isEmpty()) return 1;
        int max = 0;
        for (int key : deposits.keySet()) {
            if (key > max) max = key;
        }
        return max + 1;
    }
}
