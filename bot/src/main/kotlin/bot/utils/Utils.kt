package bot.utils

import java.text.DecimalFormat

fun Double.toPercentage(): String {
    return DecimalFormat("#.##").format(this)
}