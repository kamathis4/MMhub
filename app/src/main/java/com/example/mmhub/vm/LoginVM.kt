package com.example.mmhub.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.PreferencesDatastore
import com.example.domain.model.NetworkState
import com.example.domain.usecases.GetAccessTokenUseCase
import com.example.mmhub.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(private val getAccessTokenUseCase: GetAccessTokenUseCase,
private val datastore: PreferencesDatastore)
    :ViewModel() {
//    var loggedin = true

    private val _postStateFlow: MutableStateFlow<UIState<Nothing?>> =
        MutableStateFlow(UIState.Empty)

    val postStateFlow: StateFlow<UIState<Nothing?>> = _postStateFlow

    fun login(){
        viewModelScope.launch {
            _postStateFlow.emit(UIState.Loading)
            val token = datastore.readData("access_token")
            if (token != null) {
                Log.e("DS token", token)
            }
            if (token !="null"){
                Log.e("Success VM", "why?")
                _postStateFlow.emit(UIState.Success(null))
            }else{
                Log.e("Empty hit", "Correctly")
                _postStateFlow.emit(UIState.Empty)
            }

//            startLoginProcess(code)
        }
    }

    fun startLoginProcess(code:String){

        viewModelScope.launch(Dispatchers.IO) {
            when(val token = getAccessTokenUseCase.getUserAccessToken(code = code)){
                is NetworkState.Failure ->{
                    Log.e("Login", token.exception.toString())
                    _postStateFlow.emit(UIState.Failure(Exception("Unable to get key")))
                }
                is NetworkState.Success -> {
                    Log.e("Login", "" + token.data.access_token)
                    datastore.saveData("access_token", token.data.access_token)
                    _postStateFlow.emit(UIState.Success(null))
                }
            }
        }
    }
}