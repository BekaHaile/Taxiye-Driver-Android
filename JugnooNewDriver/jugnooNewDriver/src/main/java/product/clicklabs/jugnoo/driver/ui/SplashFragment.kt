package product.clicklabs.jugnoo.driver.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.SplashNewActivity

class SplashFragment() : Fragment() {



    companion object {
        fun newInstance(arguments: Bundle?): SplashFragment{
            val splashFragment = SplashFragment();
            splashFragment.arguments=arguments;
            return splashFragment;
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context);
        if(context is DriverSplashActivity){

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_splash,container,false);

        val intent = Intent();
        val packageName =(Spla);

        return rootView;
    }
}
