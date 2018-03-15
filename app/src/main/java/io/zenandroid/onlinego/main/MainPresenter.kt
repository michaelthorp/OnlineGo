package io.zenandroid.onlinego.main

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.zenandroid.onlinego.BuildConfig
import io.zenandroid.onlinego.ogs.ActiveGameRepository
import io.zenandroid.onlinego.ogs.OGSServiceImpl

/**
 * Created by alex on 14/03/2018.
 */
class MainPresenter (val view : MainContract.View, val activeGameRepository: ActiveGameRepository) : MainContract.Presenter {

    private val subscriptions = CompositeDisposable()

    override fun subscribe() {
        view.mainTitle = "OnlineGo"
        view.subtitle = BuildConfig.VERSION_NAME


        subscriptions.add(
                activeGameRepository.myMoveCountObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onMyMoveCountChanged)
        )
        activeGameRepository.subscribe()
    }

    private fun onMyMoveCountChanged(myMoveCount: Int) {
        if (myMoveCount == 0) {
            view.notificationsButtonEnabled = false
            view.notificationsBadgeVisible = false
            view.cancelNotification()
        } else {
            val sortedMyTurnGames = activeGameRepository.myTurnGamesList.sortedWith(compareBy { it.id })
            view.notificationsButtonEnabled = true
            view.notificationsBadgeVisible = true
            view.notificationsBadgeCount = myMoveCount.toString()
            view.updateNotification(sortedMyTurnGames)
        }
    }

    override fun unsubscribe() {
        activeGameRepository.unsubscribe()
        subscriptions.clear()
    }

    fun navigateToGameScreenById(gameId: Long) {
        subscriptions.add(
            OGSServiceImpl.instance.fetchGame(gameId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::navigateToGameScreen, this::onError)
        )
    }

    override fun onNotificationClicked() {
        view.navigateToGameScreen(activeGameRepository.myTurnGamesList[0])
    }

    private fun onError(t: Throwable) {
        Log.e(MainActivity.TAG, t.message, t)
        view.showError(t.message)
    }
}