import java.sql.*
import java.util.*

private val TABLE_NAME = "Gallery"
private val DB_PATH = "gallery.db"

class DB {

    private lateinit var connection: Connection

    init {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$DB_PATH")
            println("Connection to SQLite has been established.")

            createTable(connection)
            println("Table created")
        } catch (e: SQLException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    private fun createTable(connection: Connection) {
        val stmt = connection.createStatement()
        stmt.execute("""
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            id INT,
            src BLOB
        )
    """.trimIndent())
    }

    fun getCount(): Int {
        val stmt: Statement = connection.createStatement()
        val rs: ResultSet = stmt.executeQuery("SELECT COUNT(*) FROM $TABLE_NAME")
        while (rs.next()){
            return rs.getInt(1);
        }
        return 0
    }

    fun setImage(id: Int, src: String) {
        val stmt: Statement = connection.createStatement()
        stmt.execute("INSERT INTO $TABLE_NAME (id, src) VALUES ('$id', '$src')")
    }


    fun getPictures(from: Int): List<String> {
        val stmt: Statement = connection.createStatement()
        val rs: ResultSet = stmt.executeQuery("SELECT src FROM $TABLE_NAME WHERE id>=\"$from\"")
        return rs.use {
            generateSequence { if (rs.next()) rs.getString(1) else null }.toList()
        }
    }
}
