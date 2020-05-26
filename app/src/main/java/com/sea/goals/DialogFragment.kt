package com.sea.goals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

interface DialogFragmentListener {
    fun onDoneConfirmSpecific(goal: Goal, progress: Double)
    fun onDoneConfirm(goal: Goal)
}

class DoneDialogFragment(private val currentCard: Goal): DialogFragment() {
    lateinit var listener: DialogFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.done_dialog, container, false)
        rootView.findViewById<Button>(R.id.confirm_done).setOnClickListener {
            listener.onDoneConfirm(currentCard)
        }
        rootView.findViewById<TextView>(R.id.dialog_titel).text = currentCard.name
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //TODO check if activity implements FragmentDailyGoalListner
        listener = context as DialogFragmentListener
    }
}

class DoneSpecificDialogFragment(val currentCard: Goal): DialogFragment() {
    lateinit var listener: DialogFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.done_specific_dialog, container, false)
        rootView.findViewById<TextView>(R.id.dialog_specific_title).text = currentCard.name
        val progressView = rootView.findViewById<TextView>(R.id.dialog_specific_progress)
        progressView.text = currentCard.goal.toString()
        rootView.findViewById<TextView>(R.id.dialog_specific_unit).text = currentCard.unit
        rootView.findViewById<Button>(R.id.confirm_done_specific).setOnClickListener {
            val progress = progressView.text.toString().toDouble()
            listener.onDoneConfirmSpecific(currentCard, progress)
        }
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //TODO check if activity implements FragmentDailyGoalListner
        listener = context as DialogFragmentListener
    }
}