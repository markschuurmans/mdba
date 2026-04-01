package nl.avans.eindopdracht.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    private val applicationContext = context.applicationContext

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    fun <T> add(request: Request<T>) {
        requestQueue.add(request)
    }

    companion object {
        @Volatile
        private var instance: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton {
            return instance ?: synchronized(this) {
                instance ?: VolleySingleton(context).also { instance = it }
            }
        }
    }
}

