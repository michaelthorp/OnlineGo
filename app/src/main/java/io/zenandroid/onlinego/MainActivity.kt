package io.zenandroid.onlinego

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.android.schedulers.AndroidSchedulers
import io.zenandroid.onlinego.login.LoginActivity
import io.zenandroid.onlinego.ogs.OGSService
import io.zenandroid.onlinego.spectate.SpectateFragment


class MainActivity : AppCompatActivity() {

    @BindView(R.id.bottom_navigation) lateinit var bottomNavigation: BottomNavigationView

    private val spectateFragment = SpectateFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        bottomNavigation.setOnNavigationItemSelectedListener { selectItem(it) }
    }

    override fun onResume() {
        super.onResume()

        OGSService.instance.loginWithToken().observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    bottomNavigation.selectedItemId = R.id.navigation_spectate
                },
                { _ -> startActivity(LoginActivity.getIntent(this)) }
        )
    }

    fun selectItem(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.navigation_spectate -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, spectateFragment).commit()
                true
            }
            R.id.navigation_challenges -> {
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show()
                false
            }
            R.id.navigation_my_games -> {
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show()
                false
            }
            else -> false
        }

    }
}