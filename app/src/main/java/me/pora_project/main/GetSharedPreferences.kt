package me.pora_project.main

import android.content.Context
import android.content.SharedPreferences

class GetSharedPreferences (ctx: Context?) {
  private val USER_PREF = "USER_PREF"

  private var sp: SharedPreferences

  init {
    sp = ctx?.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)!!
  }

  fun save(list: MutableMap<String, Float>) {
    val editor = sp.edit()
    for (el in list){
      println(el)
      editor.putString(el.key, el.value.toString());
    }
    editor.apply()
  }

  fun show(list: MutableSet<String>): MutableList<Float> {
    val out = mutableListOf<Float>()
    for (el in list){
      println(el)
      out.add(sp.getString(el, "20.0")?.toFloat() ?: 20.0f)
    }
    return out;
  }
}