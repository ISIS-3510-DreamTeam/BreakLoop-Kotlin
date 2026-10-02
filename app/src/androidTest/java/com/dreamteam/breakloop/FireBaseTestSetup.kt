package com.dreamteam.breakloop

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before

abstract class FirebaseTestSetup {

    protected lateinit var auth: FirebaseAuth
    protected lateinit var firestore: FirebaseFirestore

    @Before
    fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }
}