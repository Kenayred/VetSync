package com.example.vetsync.modelo

class FirebaseDatabaseManager {
    private val database = FirebaseDatabase.getInstance()
    fun insertData(data: Any, path: String, completionListener: DatabaseReference.CompletionListener) {
        val myRef = database.getReference(path)
        myRef.setValue(data, completionListener)
    }
    fun readData(path: String, valueEventListener: ValueEventListener) {
        val myRef = database.getReference(path)
        myRef.addValueEventListener(valueEventListener)
    }
}