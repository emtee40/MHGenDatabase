package com.ghstudios.android.features.monsters

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterDamage
import com.ghstudios.android.data.classes.MonsterStatus
import com.ghstudios.android.data.classes.MonsterWeakness
import com.ghstudios.android.data.cursors.MonsterAilmentCursor
import com.ghstudios.android.data.database.DataManager

class MonsterDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val monsterData = MutableLiveData<Monster>()
    val monsterWeaknessData = MutableLiveData<List<MonsterWeakness>>()
    val monsterDamageData = MutableLiveData<List<MonsterDamage>>()
    val monsterStatusData = MutableLiveData<List<MonsterStatus>>()

    var monsterId = -1L

    fun setMonster(monsterId : Long) {
        if (this.monsterId == monsterId) {
            return
        }

        this.monsterId = monsterId

        Thread {
            // load and post monster first (high priority)
            monsterData.postValue(dataManager.getMonster(monsterId))

            // then load the rest
            monsterWeaknessData.postValue(dataManager.queryMonsterWeaknessArray(monsterId))
            monsterDamageData.postValue(dataManager.queryMonsterDamageArray(monsterId))
            monsterStatusData.postValue(dataManager.queryMonsterStatus(monsterId))
        }.start()
    }

    /**
     * Returns a live data that can be observed for a MonsterAilmentCursor.
     * Everytime this is called, a new cursor is retrieved
     */
    fun getAilments() : LiveData<MonsterAilmentCursor> {
        // todo: check if this makes a leak. If it doesn't, remove this todo
        val result = MutableLiveData<MonsterAilmentCursor>()

        Thread {
            result.postValue(dataManager.queryAilmentsFromId(monsterId))
        }.start()

        return result
    }
}