package com.example.shoppyone.frags

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.shoppyone.MainActivity
import com.example.shoppyone.db.GetProducts
import com.example.shoppyone.R
import com.example.shoppyone.databinding.FragmentProductsBinding
import com.example.shoppyone.mods.Product
import com.google.gson.Gson
import org.json.JSONObject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    public lateinit var mv: Context
    private lateinit var sharedPref: SharedPreferences

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mv = this.requireContext()
            sharedPref = activity?.getSharedPreferences("mp", Context.MODE_PRIVATE)
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onSomeEventListener")
        }

    }

    override fun onStart() {
        super.onStart()
        // System.out.println(sharedPref.getString("ls",""))
        onss()
    }

    override fun onResume() {
        super.onResume()
        //System.out.println(sharedPref.getString("ls",""))

        // System.out.println("arr res")
        // System.out.println(activity?.intent?.getStringExtra("arr"))
        onss()
        //GetProducts(mv, 1, null)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //     if(!activity?.intent?.getStringExtra("arr").isNullOrEmpty())
        //       GetProducts(mv,0, JSONObject(activity?.intent?.getStringExtra("arr")))

        val prb = _binding!!.root.findViewById<ImageButton>(R.id.ibshops)
        prb.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = android.R.animator.fade_in
                    exit = android.R.animator.fade_out
                }
            }
        }
        val nb = _binding!!.root.findViewById<ImageButton>(R.id.ibtypes)

        nb.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_ThirdFragment)
            navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = android.R.animator.fade_in
                    exit = android.R.animator.fade_out
                }
            }


        }


        var mb = _binding!!.root.findViewById<Button>(R.id.mb)
        mb.setOnClickListener {
            setState()
        }


        var mb1 = _binding!!.root.findViewById<ImageButton>(R.id.ibst)

        mb1.setOnClickListener {

            findNavController().navigate(R.id.action_toAddProductFragment)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setState() {
        var sv = (mv as Activity).findViewById<SearchView>(R.id.sview)
        var msq1 = sv.getQuery().toString()
        var editor = sharedPref.edit()
        editor.putString("msqp", msq1)
        editor.putString("lsip", "sb")
        editor.commit()


        var int = activity?.intent!!
        if (int.hasExtra("arr")) {
            int.removeExtra("arr")
        }

        onss()
    }

    private fun onss() {
        var ms = sharedPref.getString("lsip", "")
        if (ms.equals("cb") && !sharedPref.getString("lsp", "").isNullOrEmpty())
            GetProducts(mv, 0, JSONObject(sharedPref.getString("lsp", "")), null, null, null)
        else
            GetProducts(mv, 1, null, sharedPref.getString("msqp", null), null, null)
    } //



}