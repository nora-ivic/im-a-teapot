package progi.imateacup.nestaliljubimci.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import progi.imateacup.nestaliljubimci.databinding.ActivityMainBinding

lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}