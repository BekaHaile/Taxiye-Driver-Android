package product.clicklabs.jugnoo.driver.utils


import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

/**
 * This is a helper class to generate your message hash to be included in your SMS message.
 *
 * Without the correct hash, your app won't recieve the message callback. This only needs to be
 * generated once per app and stored. Then you can remove this helper class from your code.
 */
class AppSignatureHelper {
    /**
     * Get all the app signatures for the current package
     * @return
     */
    companion object {
        val TAG = "DynamiteModuleASH"

        private val HASH_TYPE = "SHA-256"
        val NUM_HASHED_BYTES = 9
        val NUM_BASE64_CHAR = 11

        fun getAppSignatures(context: Context): ArrayList<String> {
            val appCodes = ArrayList<String>()

            try {
                // Get all package signatures for the current package
                val packageName = context.packageName
                val packageManager = context.packageManager
                val signatures = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_SIGNATURES).signatures

                // For each signature create a compatible hash
                for (signature in signatures) {
                    val hash = hash(packageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Unable to find package to obtain hash.", e)
            }

            return appCodes
        }


        private fun hash(packageName: String, signature: String): String? {
            val appInfo = "$packageName $signature"
            try {
                val messageDigest = MessageDigest.getInstance(HASH_TYPE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                }
                var hashSignature = messageDigest.digest()

                // truncated into NUM_HASHED_BYTES
                hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
                // encode into Base64
                var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
                base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

                Log.i(TAG, String.format("p: %s -- h: %s", packageName, base64Hash))
                return base64Hash
            } catch (e: Exception) {
                Log.e(TAG, "hash:NoSuchAlgorithm", e)
            }

            return null
        }
    }


}