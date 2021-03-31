package product.clicklabs.jugnoo.driver.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import product.clicklabs.jugnoo.driver.BuildConfig
import java.io.File


class PhotoProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    @Nullable
    override fun getType(@NonNull uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    companion object {

        val CONTENT_PROVIDER_AUTHORITY = BuildConfig.authorities

        fun getPhotoUri(file: File): Uri {
            val outputUri = Uri.fromFile(file)
            val builder = Uri.Builder()
                    .authority(CONTENT_PROVIDER_AUTHORITY)
                    .scheme("file")
                    .path(outputUri.path)
                    .query(outputUri.query)
                    .fragment(outputUri.fragment)

            return builder.build()
        }
    }
}