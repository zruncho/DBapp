package com.example.shoppyone.db

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Spinner
import androidx.viewbinding.ViewBinding
import com.example.shoppyone.R
import com.example.shoppyone.mods.Product
import com.example.shoppyone.mods.Shop
import com.example.shoppyone.mods.SobjInterface
import com.example.shoppyone.mods.Type
import com.facebook.FacebookSdk.getApplicationContext
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.InputStream
import java.util.*

class AddProduct (c: Activity){

    private var retrofit: Retrofit? = null
    private lateinit var sharedPref: SharedPreferences
    init {

        sharedPref = c.getPreferences(Context.MODE_PRIVATE)
        retrofit = GetRetrofit(c).getRet()
    }

    public fun delO(uu: String?,type: String?) {
        var mv:SobjInterface?=null
        when(type){
            "product" -> {  mv = Product(); mv.uuid=uu }
            "shop" ->    {  mv = Shop(); mv.uuid=uu }
            "type" ->    {  mv = Type(); mv.uuid=uu }
        }
        makeRFC("del",null,null,mv)
    }

     fun addShop(selectedImage: Uri?, _binding: ViewBinding?, mv: Context, ff:String) {
        var name:String? = null
        var uuid = UUID.randomUUID().toString()
        var uriString:String? = null
        var body: MultipartBody.Part? = null


        if(selectedImage != null) {
            uriString = selectedImage?.path.toString()
            val file = File(uriString)
            val iscr: InputStream = mv.getContentResolver().openInputStream(selectedImage!!)!!
            val reqFile: RequestBody =
                RequestBody.create(MediaType.parse("image/*"), iscr.readBytes())
            iscr.close()
            body = MultipartBody.Part.createFormData("file", uuid, reqFile)
        }
       // retrofit = GetRetrofit(mv).getRet()

         var shop:SobjInterface? =null
        when(ff){
            "shops"     ->{
                name="logo"
                shop = Shop()
                shop.uuid = uuid
                shop.name  = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(
                    R.id.oiname).editText?.text.toString()
                shop.type  = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(
                    R.id.oitype).editText?.text.toString()
                shop.url   = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(
                    R.id.oiurl).editText?.text.toString()
                shop.logo  = if(selectedImage != null) "true" else "false"
                shop.owner = sharedPref.getString("um","")

            }
            "types"     ->{
                name = "types"
                shop = Type()
                shop.uuid   = uuid
                shop.name   = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oiname).editText?.text.toString()
                shop.owner  = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oiowner).editText?.text.toString()
                shop.shopid =  sharedPref.getString("shop","")
                shop.pic    = if(selectedImage != null) "true" else "false"
               // shop.type   = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oitype).editText?.text.toString()
            }
            "products"  ->{
                name = "products"
                shop = Product()
                shop.uuid   = uuid
                shop.name   = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oiname).editText?.text.toString()
                shop.price  = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oiprice).editText?.text.toString().toDouble()
                shop.off    = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oidis).editText?.text.toString()
                shop.prom   = if(_binding!!.root.findViewById<Spinner>(R.id.per_spinner).getSelectedItem().toString() =="true") 1 else 0
                shop.shopid = sharedPref.getString("shop","")
                shop.type   = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oitype).editText?.text.toString()
                shop.owner  = _binding!!.root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.oiowner).editText?.text.toString()
                shop.per    = if(_binding!!.root.findViewById<Spinner>(R.id.per_spinner).getSelectedItem().toString() =="true") 1 else 0
                shop.pic    = if(selectedImage != null) "true" else "false"
            }



        }

        if(selectedImage != null)
            makeRFC("pic", body, name, null)
            makeRFC("dbe", null, null, shop)
    }

    fun makeRFC(str:String, body: MultipartBody.Part?, name:String?, addshop: SobjInterface?) {
        lateinit var postsService:Any
        var req: Call<JsonObject?>? = null

        when(str){
        "del"-> {
            postsService = retrofit!!.create(APIInterfaceSP::class.java)
            when (addshop) {
                is Shop -> req = postsService.delShop(addshop.uuid, "delete")
                is Product -> req = postsService.delProduct(addshop.uuid, "delete")
                is Type -> req = postsService.delType(addshop.uuid, "delete")
            }
        }
        "pic"->
        {
            postsService = retrofit!!.create(APIImage::class.java)
            req = postsService.postImage(body, name)
        }
        "dbe"-> {
            postsService = retrofit!!.create(APIInterfaceSP::class.java)


            when (addshop) {
                is Shop ->
                    req = postsService.addShop(
                        addshop?.uuid,
                        addshop?.name,
                        addshop?.logo,
                        addshop?.url,
                        addshop?.owner,
                        addshop?.type,
                        "add"
                    )
                is Type ->
                    req = postsService.addType(
                        addshop?.uuid,
                        addshop?.name,
                        addshop?.owner,
                        addshop?.shopid,
                        addshop?.pic,
                        "add"
                    )

                is Product ->
                    req = postsService.addProduct(
                        addshop?.uuid,
                        addshop?.name,
                        addshop?.owner,
                        addshop?.off,
                        addshop?.prom,
                        addshop?.price,
                        addshop?.shopid,
                        addshop?.type,
                        addshop?.pic,
                        addshop?.per,
                        "add"
                    )

            }


        }}

        req?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                Log.d("rb", response.body().toString())
                val jr = JSONObject(Gson().toJson(response.body()))
                Log.d("respo",jr.get("info").toString())
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}