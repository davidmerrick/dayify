package io.github.davidmerrick.dayify.logic

import biweekly.property.DateEnd
import biweekly.property.DateStart


object DefaultDateConversionStrategy : DateConversionStrategy {
    override fun convertStartDate(date: DateStart): DateStart {
        return DateStart(
            date.value.rawComponents.toDate(),
            false
        )
    }

    override fun convertEndDate(date: DateEnd): DateEnd {
        return DateEnd(
            date.value.rawComponents.toDate(),
            false
        )
    }
}
