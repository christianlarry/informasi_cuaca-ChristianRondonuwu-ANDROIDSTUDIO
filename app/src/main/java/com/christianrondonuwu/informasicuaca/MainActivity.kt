package com.christianrondonuwu.informasicuaca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.christianrondonuwu.informasicuaca.models.CityModel
import com.christianrondonuwu.informasicuaca.models.WeatherModel
import com.pdsk.cocodroid.retrofit.ApiService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    val api_key:String = "7d6a15edadf6d08319604577b86b9931"

    lateinit var weatherIconImageView:ImageView
    lateinit var cityNameTextView: TextView
    lateinit var suhuTextView: TextView
    lateinit var weatherDescTextView: TextView
    lateinit var weatherMainTextView: TextView
    lateinit var kecepatanAnginTextView: TextView
    lateinit var kelembabanTextView: TextView
    lateinit var kemungkinanHujanTextView: TextView
    lateinit var tekananTextView: TextView
    lateinit var searchIconImageView: ImageView
    lateinit var closeBtnImageView: ImageView
    lateinit var searchModalRelativeLayout: RelativeLayout
    lateinit var searchEditText: EditText
    lateinit var cityItemContainer: LinearLayout

    fun initComponents(){
        weatherIconImageView = findViewById(R.id.weatherIconImageView)
        cityNameTextView = findViewById(R.id.cityNameTextView)
        suhuTextView = findViewById(R.id.suhuTextView)
        weatherDescTextView = findViewById(R.id.weatherDescTextView)
        weatherMainTextView = findViewById(R.id.weatherMainTextView)
        kecepatanAnginTextView = findViewById(R.id.kecepatanAnginTextView)
        kelembabanTextView = findViewById(R.id.kelembabanTextView)
        kemungkinanHujanTextView = findViewById(R.id.kemungkinanHujanTextView)
        tekananTextView = findViewById(R.id.tekananTextView)
        searchIconImageView = findViewById(R.id.searchIconImageView)
        closeBtnImageView = findViewById(R.id.closeSearchImageView)
        searchModalRelativeLayout = findViewById(R.id.searchModalRelativeLayout)
        searchEditText = findViewById(R.id.searchEditText)
        cityItemContainer = findViewById(R.id.cityItemContainer)
    }

    fun getWeather(lat:String,lon:String){
        ApiService.endpoint.getWeather(api_key,lat,lon,"id","metric").enqueue(object : Callback<WeatherModel>{
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if(response.isSuccessful){
                    val result = response.body()

                    // SET WEATHER ICON
                    val weatherIconURL = "https://openweathermap.org/img/wn/"+result?.weather?.get(0)?.icon+".png"
                    Picasso.get()
                        .load(weatherIconURL)
                        .into(weatherIconImageView);

                    cityNameTextView.setText(result?.name)
                    suhuTextView.setText(result?.main?.temp?.toInt().toString())
                    weatherDescTextView.setText(result?.weather?.get(0)?.main)
                    weatherMainTextView.setText(result?.weather?.get(0)?.description)
                    kecepatanAnginTextView.setText(String.format("%.1f",result?.wind?.speed!!*3.6)+"km/h")
                    kelembabanTextView.setText(result?.main?.humidity.toString()+"%")
                    kemungkinanHujanTextView.setText(result?.clouds?.all.toString()+"%")
                    tekananTextView.setText(result?.main?.pressure.toString()+"mbar")
                }
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Log.d("TTEST",t.stackTraceToString())
            }
        })
    }

    fun searchKotaAndSetView(search:String){
        ApiService.endpoint.getCoordByCity(api_key,search,5).enqueue(object : Callback<Array<CityModel>>{
            override fun onResponse(
                call: Call<Array<CityModel>>,
                response: Response<Array<CityModel>>
            ) {
                if(response.isSuccessful){
                    cityItemContainer.removeAllViews()

                    val result = response.body()!!
                    for(res in result){
                        Log.d("TEST",res.toString())
                        setCityListView(res.name,res.lat.toString(),res.lon.toString())
                    }
                }
            }

            override fun onFailure(call: Call<Array<CityModel>>, t: Throwable) {
                Log.d("TTEST",t.stackTraceToString())
            }
        })
    }

    fun setCityListView(name:String,lat:String,lon: String){
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        val linearLayoutPad:Int = (10 * resources.displayMetrics.density).roundToInt()
        linearLayout.setPadding(linearLayoutPad)

        val textView = TextView(this)
        textView.id = View.generateViewId()
        textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        textView.typeface = resources.getFont(R.font.montserrat_medium)
        textView.setText(name.toString())

        linearLayout.addView(textView)

        linearLayout.setOnClickListener{
            getWeather(lat,lon)
            searchModalRelativeLayout.visibility = View.GONE
            currentFocus?.clearFocus()
        }

        cityItemContainer.addView(linearLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()

        getWeather("1.4900578","124.8408708")

        searchIconImageView.setOnClickListener {
            searchModalRelativeLayout.visibility = View.VISIBLE
            currentFocus?.clearFocus()
        }

        closeBtnImageView.setOnClickListener {
            searchModalRelativeLayout.visibility = View.GONE
            currentFocus?.clearFocus()
        }

        searchEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchKotaAndSetView(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}