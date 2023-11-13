package progi.imateacup.nestaliljubimci.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.networking.ApiModule

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiModule.initRetrofit(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }





    //TODO
    // nakon sto se uspjesno napravi login i dobije se response sa tokenom
    // pozvati ApiModule.setSessionInfo(token), nakon toga ako sve radi
    // kak se spada o tokenu se vise nebi morali brinuti sljedeca 2 sata,
    // token istekne nakon 2 sata i kada se dobije response koji govori da
    // je istekao token preusmjeriti na login gdje ce se opet pozvati setSessionInfo
}