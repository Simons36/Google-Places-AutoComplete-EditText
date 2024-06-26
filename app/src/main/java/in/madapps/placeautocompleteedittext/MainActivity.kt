package `in`.madapps.placeautocompleteedittext

import my.edit.text.placesautocomplete.PlaceAPI
import my.edit.text.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import my.edit.text.placesautocomplete.listener.OnPlacesDetailsListener
import my.edit.text.placesautocomplete.model.Address
import my.edit.text.placesautocomplete.model.Place
import my.edit.text.placesautocomplete.model.PlaceDetails
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.autoCompleteEditText
import kotlinx.android.synthetic.main.activity_main.cityTextView
import kotlinx.android.synthetic.main.activity_main.compoundCodeTextView
import kotlinx.android.synthetic.main.activity_main.countryTextView
import kotlinx.android.synthetic.main.activity_main.globalCodeTextView
import kotlinx.android.synthetic.main.activity_main.latitudeTextView
import kotlinx.android.synthetic.main.activity_main.longitudeTextView
import kotlinx.android.synthetic.main.activity_main.placeIdTextView
import kotlinx.android.synthetic.main.activity_main.stateTextView
import kotlinx.android.synthetic.main.activity_main.streetTextView
import kotlinx.android.synthetic.main.activity_main.urlTextView
import kotlinx.android.synthetic.main.activity_main.utcOffsetTextView
import kotlinx.android.synthetic.main.activity_main.vicinityTextView
import kotlinx.android.synthetic.main.activity_main.zipCodeTextView

/**
 * The main activity show cases an example of how to use the places auto complete api
 */
class MainActivity : AppCompatActivity() {

  val placesApi = PlaceAPI.Builder()
    .apiKey("YOUR_API_KEY")
    .build(this@MainActivity)

  var street = ""
  var city = ""
  var state = ""
  var country = ""
  var zipCode = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    autoCompleteEditText.setAdapter(PlacesAutoCompleteAdapter(this, placesApi))
    autoCompleteEditText.onItemClickListener =
      AdapterView.OnItemClickListener { parent, _, position, _ ->
        val place = parent.getItemAtPosition(position) as Place
        autoCompleteEditText.setText(place.description)
        getPlaceDetails(place.id)
      }
  }

  private fun getPlaceDetails(placeId: String) {
    placesApi.fetchPlaceDetails(placeId, object :
      OnPlacesDetailsListener {
      override fun onError(errorMessage: String) {
        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
      }

      override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
        setupUI(placeDetails)
      }
    })
  }

  private fun setupUI(placeDetails: PlaceDetails) {
    val address = placeDetails.address
    parseAddress(address)
    runOnUiThread {
      streetTextView.text = street
      cityTextView.text = city
      stateTextView.text = state
      countryTextView.text = country
      zipCodeTextView.text = zipCode
      latitudeTextView.text = placeDetails.lat.toString()
      longitudeTextView.text = placeDetails.lng.toString()
      placeIdTextView.text = placeDetails.placeId
      urlTextView.text = placeDetails.url
      utcOffsetTextView.text = placeDetails.utcOffset.toString()
      vicinityTextView.text = placeDetails.vicinity
      compoundCodeTextView.text = placeDetails.compoundPlusCode
      globalCodeTextView.text = placeDetails.globalPlusCode
    }
  }

  private fun parseAddress(address: ArrayList<Address>) {
    (0 until address.size).forEach { i ->
      when {
        address[i].type.contains("street_number") -> street += address[i].shortName + " "
        address[i].type.contains("route") -> street += address[i].shortName
        address[i].type.contains("locality") -> city += address[i].shortName
        address[i].type.contains("administrative_area_level_1") -> state += address[i].shortName
        address[i].type.contains("country") -> country += address[i].shortName
        address[i].type.contains("postal_code") -> zipCode += address[i].shortName
      }
    }
  }
}