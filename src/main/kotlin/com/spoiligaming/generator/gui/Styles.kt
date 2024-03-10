import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.SkinBase

object Styles {
   /* fun applyComboBoxStyle(comboBox: ComboBox<String>) {
        val popupControl = comboBox.skin
            .let { it as? SkinBase<*> }
            ?.childrenUnmodifiable
            ?.find { it is ListView<*> } as? ListView<*>

        popupControl?.style = """
            -fx-background-color: #4C4C4C;
            -fx-text-fill: #FFFFFF;
            -fx-border-color: transparent;
        """.trimIndent()

        val arrowButton = comboBox.lookup(".arrow-button")
        arrowButton?.style = """
            -fx-background-color: white;
            -fx-shape: "M480-362q-8 0-15-2.5t-13-8.5L268-557q-11-11-11-28t11-28q11-11 28-11t28 11l156 156 156-156q11-11 28-11t28 11q11 11 11 28t-11 28L508-373q-6 6-13 8.5t-15 2.5Z"; /* Replace with your SVG path */
            -fx-scale-x: 0.6;
            -fx-scale-y: 0.4;
        """.trimIndent()

        val listCellHover = comboBox.lookupAll(".list-cell").find { it.isHover }
        listCellHover?.style = "-fx-text-fill: #E85D9B;"
    }*/
}