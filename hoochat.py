#!/usr/bin/python

from flask import Flask, render_template, redirect, url_for, request, session, flash
import mysql.connector
import re, os

app = Flask(__name__)

#Generate a secret key
app.secret_key = os.urandom(24)

cnx = mysql.connector.connect(user='root', password='root', host='127.0.0.1', database='dbo')
cursor = cnx.cursor()

### Helper functions for validating input ###

def is_email_address_valid(email):
    if not re.match("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+{1,45})*$", email):
        return False
    return True

def is_password_valid(password):
    if not re.match(r'[A-Za-z0-9]{6,45}', password):
        return False
    return True

def email_exists(email):
    args = (email)
    cursor.execute("CALL getUser(%s)", (email,))
    results = cursor.fetchone()
    if results:
        return True
    return False    

def check_password(_email, _password):
    query = ("SELECT pass FROM dbo.Users WHERE email = %s")
    cursor.execute(query, (_email,))
    data = cursor.fetchall()
    for pswd in data:
        if pswd == _password:
            return True
        return False    

### ROUTES ###

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/account')
def account():
    return render_template('account.html')

@app.route('/signup', methods=['GET', 'POST'])
def signup():
    error = None

    # validate the received values
    if request.method == 'POST':
        _fname = request.form['fname'].strip()
        _lname = request.form['fname'].strip()
        _email = request.form['email'].strip()
        _password = request.form['password'].strip()
        _phone = request.form['phone'].strip()

        if not _fname or not _lname or not _email or not _password or not _phone: 
            error = 'Please fill in all fields'          
 
        if not error:
            if not is_email_address_valid(_email):
                error = "Please enter a valid email address" 
            if not is_password_valid(_password):
                error = "Password must be between 6-45 alphanumeric characters"
            
        if not error:

            # Add user to database

            # Send verification email

            flash('Thank you for signing up!')
            return redirect(url_for('index'))

    return render_template('signup.html', error=error)

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        _email = request.form['email'].strip()
        _password = request.form['password']

        if not _email or not _password: 
            error = 'Please fill in all fields'          
 
        if not error:
            if not is_email_address_valid(_email):
                error = "Email or password is invalid" 
            if not is_password_valid(_password):
                error = "Email or password is invalid"

            # Check DB for email and password
            if not email_exists(_email):
                error = "Email or password is invalid"
            else:
                if not check_password(_email, _password):
                    error = "Email or password is invalid"   

        if not error:
            session['logged_in'] = True
            return redirect(url_for('account'))
    return render_template('login.html', error=error)

@app.route('/logout')
def logout():
    session.pop('logged_in', None)
    flash('You have been logged out')
    return redirect(url_for('index'))

@app.route('/changePasswd', methods=['GET', 'POST'])
def changePasswd():
    error = None
    if request.method == 'POST':
        _oldpass = request.form['oldpassword'].strip()
        _newpass = request.form['newpassword'].strip()

        # Check old password
        if not is_password_valid(_oldpass):
            error = "Invalid password"

        ## Check DB

        # Validate new password
        if not is_password_valid(_newpass):
            error = "Password must be between 6-45 alphanumeric characters"
        
        if not error:
            ## Save new password to db
       
            flash('Your password has been changed')
            return redirect(url_for('account'))
    return render_template('changePasswd.html', error=error)

if __name__ == "__main__":
    app.debug = False
    app.run()
