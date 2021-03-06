package com.example.pillclassificationkeras.user

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.pillclassificationkeras.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment() {
    private var email: String = ""
    private var username: String = ""
    private var id: String = ""
    private var password: String = ""
    private var confirm_pass: String = ""
    private var username_list: MutableList<String> = mutableListOf()

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddUser>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        val signup_submit = view.findViewById<Button>(R.id.signup_submit_btn)

        signup_submit.setOnClickListener{
            email = email_input.text.toString()
            username = user_input.text.toString()
            id = id_input.text.toString()
            password = pass_input.text.toString()
            confirm_pass = conpass_input.text.toString()

            if (username == "") {
                Toast.makeText(context, "Input username", Toast.LENGTH_SHORT).show()
            }
            if (email == "" || !email.contains("@")) {
                Toast.makeText(context, "Input email", Toast.LENGTH_SHORT).show()
            }
            if (password == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            if (confirm_pass == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() && confirm_pass.isNotEmpty()) {
                if (username in username_list) {
                    Toast.makeText(context, "Please change username.", Toast.LENGTH_SHORT).show()
                }
                else if (password != confirm_pass) {
                    Toast.makeText(context, "Incorrect password.", Toast.LENGTH_SHORT).show()
                }
                else if (password.length < 4) {
                    Toast.makeText(context, "Password is too short.", Toast.LENGTH_SHORT).show()
                }
                else {
                    saveData()
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        dataReference = FirebaseDatabase.getInstance().getReference("User")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        username_list.add(message.username)
                    }
                }
            }
        })

        return view
    }

    private fun saveData() {
        val messageId = dataReference.push().key
        val journalEntry1 = AddUser(username, id, password, email)
        dataReference.child(messageId).setValue(journalEntry1).addOnCompleteListener {
            Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
        }
    }
}
