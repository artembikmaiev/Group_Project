package com.example.project.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.Calendar

/**
 * Головна база даних додатку
 * Містить таблиці для користувачів, продуктів та щоденного прогресу
 * Використовує Room для зберігання даних
 */
@Database(
    entities = [User::class, Food::class, DailyProgress::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodDao(): FoodDao
    abstract fun dailyProgressDao(): DailyProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Отримання єдиного екземпляру бази даних
         * Використовує паттерн Singleton для забезпечення єдиного екземпляру
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback для ініціалізації бази даних
     * Заповнює базу початковими даними при першому запуску
     */
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.foodDao())
                    populateTestProgressData(database.dailyProgressDao())
                }
            }
        }

        /**
         * Заповнення бази даних початковим списком продуктів
         * Додає базовий набір продуктів з їх калорійністю
         */
        suspend fun populateDatabase(foodDao: FoodDao) {
            val foodList = listOf(
                Food(name = "Куряча грудка", calories = 165),
                Food(name = "Яловичина", calories = 250),
                Food(name = "Свинина", calories = 242),
                Food(name = "Риба лосось", calories = 208),
                Food(name = "Тунець", calories = 132),
                Food(name = "Яйця", calories = 155),
                Food(name = "Творог", calories = 120),
                Food(name = "Йогурт", calories = 59),
                Food(name = "Молоко", calories = 42),
                Food(name = "Кефір", calories = 40),
                Food(name = "Сир твердий", calories = 402),
                Food(name = "Сир фета", calories = 264),
                Food(name = "Рис", calories = 130),
                Food(name = "Гречка", calories = 132),
                Food(name = "Овсянка", calories = 68),
                Food(name = "Макарони", calories = 158),
                Food(name = "Картопля", calories = 77),
                Food(name = "Морква", calories = 41),
                Food(name = "Броколі", calories = 34),
                Food(name = "Шпинат", calories = 23),
                Food(name = "Яблука", calories = 52),
                Food(name = "Банани", calories = 89),
                Food(name = "Апельсини", calories = 47),
                Food(name = "Виноград", calories = 69),
                Food(name = "Полуниця", calories = 32),
                Food(name = "Горіхи волоські", calories = 654),
                Food(name = "Мігдаль", calories = 579),
                Food(name = "Арахіс", calories = 567),
                Food(name = "Оливкова олія", calories = 884),
                Food(name = "Соняшникова олія", calories = 884),
                Food(name = "Авокадо", calories = 160),
                Food(name = "Томати", calories = 18),
                Food(name = "Огірки", calories = 15),
                Food(name = "Цибуля", calories = 40),
                Food(name = "Часник", calories = 149),
                Food(name = "Мед", calories = 304),
                Food(name = "Цукор", calories = 387),
                Food(name = "Шоколад чорний", calories = 545),
                Food(name = "Шоколад молочний", calories = 545),
                Food(name = "Печиво", calories = 417),
                Food(name = "Хліб білий", calories = 265),
                Food(name = "Хліб чорний", calories = 259),
                Food(name = "Кукурудза", calories = 86),
                Food(name = "Горох", calories = 81),
                Food(name = "Чечевиця", calories = 116),
                Food(name = "Квасоля", calories = 333),
                Food(name = "Соя", calories = 446),
                Food(name = "Тофу", calories = 76),
                Food(name = "Суші", calories = 150)
            )
            foodDao.insertAll(foodList)
        }

        suspend fun populateTestProgressData(dailyProgressDao: DailyProgressDao) {
            // Очищаємо таблицю перед додаванням тестових даних
            dailyProgressDao.deleteAllDailyProgress(1)

            val calendar = Calendar.getInstance()
            calendar.set(2025, Calendar.MAY, 1) // Встановлюємо 1 травня 2025
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Генеруємо 30 записів за травень
            repeat(25) { day ->
                val date = calendar.time
                
                // Генеруємо випадкові значення для прогресу
                val weightProgress = 70 + (Math.random() * 5).toInt() // 70-75 кг
                val weightTarget = 68
                val distanceProgress = (Math.random() * 10).toInt() // 0-10 км
                val distanceTarget = 5
                val waterProgress = (Math.random() * 2).toInt() // 0-2 л
                val waterTarget = 2
                val caloriesProgress = 1500 + (Math.random() * 1000).toInt() // 1500-2500 ккал
                val caloriesTarget = 2000

                val dailyProgress = DailyProgress(
                    userId = 1,
                    date = date,
                    weightProgress = weightProgress,
                    weightTarget = weightTarget,
                    distanceProgress = distanceProgress,
                    distanceTarget = distanceTarget,
                    waterProgress = waterProgress,
                    waterTarget = waterTarget,
                    caloriesProgress = caloriesProgress,
                    caloriesTarget = caloriesTarget
                )

                dailyProgressDao.insertDailyProgress(dailyProgress)
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Переходимо до наступного дня
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 