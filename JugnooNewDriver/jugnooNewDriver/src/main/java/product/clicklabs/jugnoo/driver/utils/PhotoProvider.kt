package product.clicklabs.jugnoo.driver.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import java.io.File


class PhotoProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    @Nullable
    override fun query(@NonNull uri: Uri, @Nullable projection: Array<String>, @Nullable selection: String, @Nullable selectionArgs: Array<String>, @Nullable sortOrder: String): Cursor? {
        return null
    }

    @Nullable
    override fun getType(@NonNull uri: Uri): String? {
        return null
    }

    @Nullable
    override fun insert(@NonNull uri: Uri, @Nullable values: ContentValues): Uri? {
        return null
    }

    override fun delete(@NonNull uri: Uri, @Nullable selection: String, @Nullable selectionArgs: Array<String>): Int {
        return 0
    }

    override fun update(@NonNull uri: Uri, @Nullable values: ContentValues, @Nullable selection: String, @Nullable selectionArgs: Array<String>): Int {
        return 0
    }

    companion object {

        val CONTENT_PROVIDER_AUTHORITY = "product.clicklabs.jugnoo.driver.utils.PhotoProvider"

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