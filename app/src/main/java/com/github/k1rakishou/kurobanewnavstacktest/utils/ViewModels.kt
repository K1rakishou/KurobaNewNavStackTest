package com.github.k1rakishou.kurobanewnavstacktest.utils

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

@MainThread
fun <VM : ViewModel> ComponentActivity.myViewModels(
  vmClass: KClass<VM>,
  factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
  val factoryPromise = factoryProducer ?: {
    defaultViewModelProviderFactory
  }

  return ViewModelLazy(vmClass, { viewModelStore }, factoryPromise)
}
