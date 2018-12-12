package product.clicklabs.jugnoo.driver.widgets

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.tokenautocomplete.TokenCompleteTextView

import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.utils.Fonts

/**
 * Created by shankar on 2/24/16.
 */
class ContactsCompletionView(context: Context, attrs: AttributeSet) : TokenCompleteTextView<ContactBean>(context, attrs) {

    override fun getViewForObject(contactBean: ContactBean): View {
        val l = getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = l.inflate(R.layout.contact_token, this@ContactsCompletionView.parent as ViewGroup, false) as LinearLayout
        (view.findViewById<View>(R.id.name) as TextView).typeface = Fonts.mavenLight(context)
        (view.findViewById<View>(R.id.name) as TextView).text = contactBean.name
        return view
    }

    override fun defaultObject(completionText: String): ContactBean {
        return ContactBean("", "", "", "")
    }

}