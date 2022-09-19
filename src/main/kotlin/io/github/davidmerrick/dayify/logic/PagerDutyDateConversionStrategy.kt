package io.github.davidmerrick.dayify.logic

import biweekly.property.DateEnd
import biweekly.property.DateStart
import biweekly.util.ICalDate
import java.time.temporal.ChronoUnit


object PagerDutyDateConversionStrategy : DateConversionStrategy {
    override fun convertStartDate(date: DateStart): DateStart {
        return DateStart(
            date.value.rawComponents.toDate(),
            false
        )
    }

    /**
     * Returns a Date that's 1 day after DateEnd.
     * This is a workaround for end dates being exclusive as per RFC 2445
     */
    override fun convertEndDate(dateEnd: DateEnd): DateEnd {
        val dateInstant = dateEnd.value.rawComponents
            .toDate()
            .toInstant()
            .plus(1L, ChronoUnit.DAYS)
        return DateEnd(
            ICalDate.from(dateInstant),
            false
        )
    }
}
