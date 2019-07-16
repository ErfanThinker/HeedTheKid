package net.crowmaster.cardasmarto.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_preferences.*

import net.crowmaster.cardasmarto.R
import net.crowmaster.cardasmarto.constant.Constants
import com.sdsmdg.tastytoast.TastyToast


/**
 * Created by root on 6/23/16.
 */
class PreferencesFragment : Fragment() {
    private var sp: SharedPreferences? = null
    private var spe: SharedPreferences.Editor? = null
    private var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        sp = activity!!.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        if (sp!!.contains(Constants.SP_CHILD_NAME)) {
            et_preferences_child_name!!.editText!!.setText(sp!!.getString(Constants.SP_CHILD_NAME, ""))
        }
        if (sp!!.contains(Constants.SP_CHILD_AGE)) {
            val age = if(sp!!.getInt(Constants.SP_CHILD_AGE, -1) != -1)
                sp!!.getInt(Constants.SP_CHILD_AGE, -1).toString() else ""
            et_preferences_age!!.editText!!.setText( age )
        }

        if (sp!!.contains(Constants.SP_PHONE)) {
            val phone = if(sp!!.getString(Constants.SP_PHONE, null).isNullOrBlank())
                "" else sp!!.getString(Constants.SP_PHONE, null)
            et_preferences_phone!!.editText!!.setText( phone )
        }

        if (sp!!.contains(Constants.SP_EMAIL)) {
            val email = if(sp!!.getString(Constants.SP_EMAIL, null).isNullOrBlank())
                "" else sp!!.getString(Constants.SP_EMAIL, null)
            et_preferences_email!!.editText!!.setText( email )
        }

        if (sp!!.contains(Constants.SP_CHILD_GENDER)) {
            val gender = sp!!.getBoolean(Constants.SP_CHILD_GENDER, true)
            rb_preferences_child_sex_male.isChecked = gender
            rb_preferences_child_sex_female.isChecked = !gender
        }

        if (sp!!.contains(Constants.SP_AUTISM_RELATIVES)) {
            val autRel = sp!!.getBoolean(Constants.SP_AUTISM_RELATIVES, true)
            rb_preferences_precedent_autism_pos.isChecked = autRel
            rb_preferences_precedent_autism_neg.isChecked = !autRel
        }


    }

    fun saveInfo() {
        spe = sp!!.edit()

        if (et_preferences_child_name != null && et_preferences_child_name!!.editText!!.text.toString().isNotBlank()) {
            spe!!.putString(Constants.SP_CHILD_NAME, et_preferences_child_name!!.editText!!.text.toString().trim()).apply()
        }

        if (et_preferences_phone != null && et_preferences_phone!!.editText!!.text.toString().isNotBlank()) {
            if (checkPhoneValidation(et_preferences_phone!!.editText!!.text.toString().trim()))
                spe!!.putString(Constants.SP_PHONE, et_preferences_phone!!.editText!!.text.toString().trim()).apply()
            else
                TastyToast.makeText(activity, getString(R.string.invalid_phone), TastyToast.LENGTH_SHORT, TastyToast.WARNING)
        }

        if (et_preferences_email != null && et_preferences_email!!.editText!!.text.toString().isNotBlank()) {
            spe!!.putString(Constants.SP_EMAIL, et_preferences_email!!.editText!!.text.toString().trim()).apply()

        }

        if (et_preferences_age != null && et_preferences_age!!.editText!!.text.toString().isNotBlank()) {
            spe!!.putInt(Constants.SP_CHILD_AGE, et_preferences_age!!.editText!!.text.toString().trim().toInt()).apply()
        }

        if (rb_preferences_child_sex_female != null &&
                (rb_preferences_child_sex_female.isChecked || rb_preferences_child_sex_male.isChecked)) {
            spe!!.putBoolean(Constants.SP_CHILD_GENDER, rb_preferences_child_sex_male.isChecked).apply()
        }

        if (rb_preferences_precedent_autism_pos != null &&
                (rb_preferences_precedent_autism_pos.isChecked || rb_preferences_precedent_autism_neg.isChecked)) {
            spe!!.putBoolean(Constants.SP_AUTISM_RELATIVES, rb_preferences_precedent_autism_pos.isChecked).apply()
        }

    }

    fun checkPhoneValidation(phoneNum:String):Boolean {
//        val validNumber = Regex("^[+]?[0-9]{8,15}$")
        val validNumber = Regex("^[0-9]{3}[0-9]{4}[0-9]{4}$")
        return phoneNum.matches(validNumber)
    }
}
