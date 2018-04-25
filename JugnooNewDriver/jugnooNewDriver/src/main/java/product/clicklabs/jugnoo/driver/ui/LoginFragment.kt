package product.clicklabs.jugnoo.driver.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.R

class LoginFragment():Fragment() {



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_login,container,false);


        return rootView;
    }




}
