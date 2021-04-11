import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDialogElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.*
import kotlinx.html.*
import kotlinx.html.dom.create
import org.w3c.dom.HTMLInputElement
import org.w3c.xhr.XMLHttpRequest


fun main() {
    window.onload = {
        val canvas = document.getElementById("canvas") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val gallery = document.getElementById("gallery") as HTMLDialogElement
        val buttonEraser = document.getElementById("button-eraser") as HTMLButtonElement
        val buttonBrush = document.getElementById("button-brush") as HTMLButtonElement
        val buttonClearAll = document.getElementById("button-clear-all") as HTMLButtonElement
        val buttonGallery = document.getElementById("button-gallery") as HTMLButtonElement
        val buttonSave = document.getElementById("button-save") as HTMLButtonElement
        val range = document.getElementById("range") as HTMLInputElement
        val color = document.getElementById("color") as HTMLInputElement
        val buttonCloseGallery = document.getElementById("button-close") as HTMLButtonElement

        //set canvas
        canvas.width = canvas.clientWidth
        canvas.height = canvas.clientHeight
        context.imageSmoothingEnabled = false

        var lastColor = "black"
        context.lineWidth = range.value.toDouble()
        var count = gallery.children.length - 1
        setBoard(buttonBrush, buttonEraser)

        //draw
        var mouseX: Double
        var mouseY: Double
        var draw = false
        canvas.addEventListener("mousedown", { e ->
            buttonSaveDefault(buttonSave)

            val event = e as MouseEvent
            val rect = canvas.getBoundingClientRect()
            mouseX = (event.clientX - rect.left) / (rect.right - rect.left) * canvas.width
            mouseY = (event.clientY - rect.top) / (rect.bottom - rect.top) * canvas.height
            draw = true
            context.beginPath()
            context.moveTo(mouseX, mouseY)
            context.lineJoin = CanvasLineJoin.Companion.ROUND
            context.lineCap = CanvasLineCap.Companion.ROUND
            context.stroke()
        })
        canvas.addEventListener("mousemove", { e ->
            buttonSaveDefault(buttonSave)
            if (draw == true) {
                val event = e as MouseEvent
                val rect = canvas.getBoundingClientRect()
                mouseX = (event.clientX - rect.left) / (rect.right - rect.left) * canvas.width
                mouseY = (event.clientY - rect.top) / (rect.bottom - rect.top) * canvas.height
                context.lineTo(mouseX, mouseY);
                context.lineJoin = CanvasLineJoin.Companion.ROUND
                context.lineCap = CanvasLineCap.Companion.ROUND
                context.stroke();
            }
        })
        canvas.addEventListener("mouseup", { e ->
            buttonSaveDefault(buttonSave)
            val event = e as MouseEvent
            val rect = canvas.getBoundingClientRect()
            mouseX = (event.clientX - rect.left) / (rect.right - rect.left) * canvas.width
            mouseY = (event.clientY - rect.top) / (rect.bottom - rect.top) * canvas.height
            context.lineTo(mouseX, mouseY);
            context.lineJoin = CanvasLineJoin.Companion.ROUND
            context.lineCap = CanvasLineCap.Companion.ROUND
            context.stroke();
            context.closePath();
            draw = false;
        })

        buttonEraser.addEventListener("click", {
            setBoard(buttonEraser, buttonBrush)
            lastColor = context.strokeStyle
            context.strokeStyle = "white"
        })
        buttonBrush.addEventListener("click", {
            setBoard(buttonBrush, buttonEraser)
            context.strokeStyle = lastColor
        })
        buttonClearAll.addEventListener("click", {
            context.clearRect(0.0, 0.0, canvas.width * 1.0, canvas.height * 1.0);
            buttonSaveDefault(buttonSave)
        })

        buttonSave.addEventListener("click", {
            val http = XMLHttpRequest()
            http.open("POST", "/save", true)
            http.onload = {
                if (http.status in 200..399) {
                    //TODO
                }
            }
            http.send(canvas.toDataURL("image/png").toString())
            buttonSaveChanged(buttonSave)
        })

        buttonGallery.addEventListener("click", {
            val http = XMLHttpRequest()
            http.open("GET", "/gallery?from=${count}")
            http.onload = {
                if (http.status in 200..399) {
                    val str = http.responseText
                    val imagies = str.split(" ")
                    for (img in imagies) {
                        if (img == "") {
                            continue
                        }
                        val image = document.create.input {
                            type = InputType.image
                            classes = setOf("colour-board", "image")
                            height = "100px"
                            src = "$img"
                        }
                        gallery.appendChild(image)
                        count = gallery.children.length - 1
                    }
                }
            }
            http.send()
            gallery.show()
        })
        buttonCloseGallery.addEventListener("click", {
            gallery.close();
        })

        range.addEventListener("change", {
            context.lineWidth = range.value.toDouble()
        })

        color.addEventListener("input", {
            setBoard(buttonBrush, buttonEraser)
            context.strokeStyle = color.value
        })
        color.addEventListener("change", {
            setBoard(buttonBrush, buttonEraser)
            context.strokeStyle = color.value
        })
    }
}

fun setBoard(first: HTMLButtonElement, second: HTMLButtonElement) {
    first.classList.add("blue-board")
    second.classList.remove("blue-board")
}

fun buttonSaveDefault(buttonSave: HTMLButtonElement) {
    buttonSave.innerText = "Save"
    buttonSave.classList.remove("blue-board")
    buttonSave.disabled = false
}

fun buttonSaveChanged(buttonSave: HTMLButtonElement) {
    buttonSave.innerText = "Saved"
    buttonSave.classList.add("blue-board")
    buttonSave.disabled = true
}

external fun encodeURIComponent(s: String): String