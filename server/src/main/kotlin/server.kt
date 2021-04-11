import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import java.io.File
import io.ktor.request.*
import java.util.*


fun Application.main() {
    val db = DB()

    routing {
        post("/save") {
            val image = call.receiveText()
            db.setImage(db.getCount(), "$image")
            call.respondText("ok")
        }

        get("/gallery") {
            val from: Int = (call.parameters["from"]!!).toString().toInt()
            var response = ""
            if (from < db.getCount()) {
                val array: List<String> = db.getPictures(from)
                for (src in array) {
                    response = response + " " + src
                }
            }
            call.respondText(response)
        }

        get("/") {
            call.respondHtml {
                head {
                    title {
                        +"My Drawing"
                    }
                    link {
                        href = "/favicon.apng"
                        rel = "icon"
                    }
                    link {
                        href = "./css/drawing.css"
                        rel = "stylesheet"
                    }
                    script {
                        src = "/client.js"
                    }
                }

                body {
                    dialog {
                        id = "gallery"
                        classes = setOf("colour-board")
                        button {
                            id = "button-close"
                            classes = setOf("contain-text")
                            type = ButtonType.button
                            +"× Close"
                        }
                    }
                    p {
                        id = "name"
                        +"MyDrawing"
                    }
                    div {
                        button {
                            id = "button-brush"
                            classes = setOf("contain-text")
                            type = ButtonType.button
                            +"Brush"
                        }
                        button {
                            id = "button-eraser"
                            classes = setOf("contain-text")
                            type = ButtonType.button
                            +"Eraser"
                        }
                        button {
                            id = "button-clear-all"
                            classes = setOf("contain-text")
                            type = ButtonType.button
                            +"Clear"
                        }
                        input {
                            id = "range"
                            type = InputType.range
                            min = "5"
                            max = "200"
                            step = "10"
                            value = "10"
                        }
                        input {
                            id = "color"
                            type = InputType.color
                        }
                        button {
                            id = "button-save"
                            classes = setOf("contain-text", "button-right")
                            type = ButtonType.button
                            +"Save "
                        }
                        button {
                            id = "button-gallery"
                            classes = setOf("contain-text", "button-right")
                            type = ButtonType.button
                            +"Gallery"
                        }
                    }
                    canvas {
                        style = "/background-squares.jpg"
                        id = "canvas"
                        classes = setOf("colour-board")
                    }
                    footer {
                        classes = setOf("contain-text")
                        +"contact me in\ntg: @marychatte"
                    }
                }
            }
        }

        static("/") {
            resources("/")
        }
    }
}