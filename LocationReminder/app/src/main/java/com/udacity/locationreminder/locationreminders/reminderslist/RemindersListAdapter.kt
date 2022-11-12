package com.udacity.locationreminder.locationreminders.reminderslist

import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseRecyclerViewAdapter


class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}