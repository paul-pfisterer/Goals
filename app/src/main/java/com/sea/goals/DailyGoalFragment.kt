package com.sea.goals

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_create_daily.view.*

/**
 * A simple [Fragment] subclass.
 */
class DailyGoalFragment : Fragment() {
    lateinit var navBack: Button
    lateinit var navFor: Button
    lateinit var submitButton: Button
    lateinit var unit: TextView
    lateinit var value: TextView
    lateinit var listener: FragmentDailyGoalListener

    /**
     * Interface dass vom Listner, also von Create Activity implementiert werden muss
     */
    interface FragmentDailyGoalListener {
        fun onSubmitDailyGoalSend(value: String, unit: String);
        fun onChangeFrag(forward: Boolean, index: Int)
    }

    /**
     * Beim Erstellen des Fragments, wird eine View gewählt und Felder gefüllt
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_daily, container, false)
        //Speichern von Views in Veriablen
        navBack = view.arrow_backward
        navFor = view.arrow_forward
        value = view.createValue
        unit = view.createUnit
        submitButton = view.createButton
        //Events für bestimmte Views
        submitButton.setOnClickListener {
            var value = value.text.toString()
            var unit = unit.text.toString()
            listener.onSubmitDailyGoalSend(value, unit)
        }
        navBack.setOnClickListener {
            listener.onChangeFrag(false, 1)
        }
        navFor.setOnClickListener{
            listener.onChangeFrag(true, 1)
        }
        return view
    }

    /**
     * Listner wird gesetzt, hier CreateGoalActivity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //TODO check if activity implements FragmentDailyGoalListner
        listener = context as FragmentDailyGoalListener
    }

    override fun onDetach() {
        super.onDetach()
        //TODO Listener auf null setzen
    }

}
