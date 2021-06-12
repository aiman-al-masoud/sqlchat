# sqlchat
A simple db-based instant messaging service.

# getting started:

You need to have a mysql server up and running for this app to work. 

You can create an account online for a remote server (sometimes even for free),
or set your own mysql server on your personal computer*.

After you create the database, however you decide to do that, you will need to make sure that you have these
4 pieces of information readily at your disposal:

### domain : 
this could be your IP, or a domain name for an online mysql hosting service. 

### port : 
(By default) 3306.

### username : 
the username you created (eg: root), or which was provided by the online service. 

### password : 
the aforementioned user's password.

### schema : 
the schema all of the tables are gonna spawn in. 
(Note that: in this simple project all of the tables go in one schema).


When you do have these information, you can run the jar with the following argument:

java -jar sqlchat.jar config

This will prompt you to enter the four parameters step by step.

Alternatively, you can manually create and fill the res/settings/netConfig file, filling 
it up with the information in this format:

domain : example.example.net

port : 3306 

username : example_user

password : example_password

schema : example_schema


## When you're done setting your connection up, you can create an account on the server you just configured:

java -jar sqlchat.jar signup

This will prompt you to choose a new username and a new password.

## Now you can launch the app:

java -jar sqlchat.jar

It will prompt you to enter your password.

## Connecting to others:
You're done! Now you can send the 4 configuration parameters to a friend (through some safe means, possibly 
on a piece of paper), and after they're done setting up the app and creating a new account, you two
can start chatting! Your conversations are NOT kept on the server once you download them, and, in the 
meanwhile, they're encrypted with 300-digit (decimal) RSA.








*In this second case, to use the app with people that aren't connected to your
local network, you need to make sure that your router supports port-forwarding 
(or something similar) if you have NAT. Also, it would'nt be wise to 
distribute your IP address to others, so maybe getting a DNS domain
would be a good idea.
