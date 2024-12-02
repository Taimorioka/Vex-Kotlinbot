package org.example

import dev.vexide.hydrozoa.CompetitionRuntime

fun main() {
    CompetitionRuntime.start{ Robot(it) }
}