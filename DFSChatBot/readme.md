# Contents
-------------
 * Introduction
 * Installation
 * Configuration
 * Features
 * Examples
 * FAQ
 * Contact Us

# Introduction
--------------
With this program one can create custom commands on twitch that can respond with text, selection from a list, and value of a counter.  All the
features can be accessed within the Graphical User Interface (GUI) and most of the commands can be accessed within twitch chat via chat
commands.  The program also utilizes security feature based on who entered the command. 

# Installation
----------------
You can pull the latest version of the executable JAR file at https://github.com/DrunkFutureSelf/DFSChatbot/releases. The
It is advised that you create a twitch user specifically for this bot.  Once your desired user has been created get an OAuth key by logging into your
account at https://twitchapps.com/tmi/.  Within the GUI click on File-> Configuration and enter your Bot user and Oauth key.  The Database
Location field can be filled in with an existing DFS bot database or new file name.  If the file name is new the program will create some basic
commands that allow you to start adding commands via twitch (see Features section below).  The chat to join will be the username you stream
on (i.e. which channel you want the bot listening in). 

# Configuration
-------------------
Aside from the configuration specified in the Installation section you are able to specify user access within the File-> User area of the GUI.  This
area allows specific users to be enrolled into specific groups, allowing your mods to create commands and general audience to run
information commands. More on this in features section.  The last bit of configuration (and the most important) is the Commands area.
Commands are maintained under File-> Commands.  There are four types of commands that you can use (See features for details).    

# Features
------------
For this section I will be providing some examples of use and some example commands.  I will be leaving out the prefix since that can be
configured differently for personal use.

Types of commands:
 * Basic Response: will return text to your twitch channel.
     - An example of this would be a discord invite.
 * Counter: will keep a count of occurrences of something happening and will return that count as well as a saying to your twitch channel.
     - An example of this would be a death counter.
 * Core Features: will allow you and your selected chat members to add and edit commands from within twitch chat.  These command names
    may be changed within the GUI under File->Commands: Core Features tab.  By default they we include: addcom, editcom, addlist, and
    addcounter.  The command addcom and addcounter will create a Basic Response and Counter respectively, while editcom will only change
    the message sent to twitch.  In order to change the value of the counter or the name of the command you will need to use the edit feature of
    the GUI.  The addlist is the most complicated core function on this list; with addlist the program will:
     - Create a new command called add<list name> this will allow you and your chat to add new items to your list.
     - Create a command called what the list is: this will allow your viewers to pull specific items from that list or a random value if none were
        provided.

An example of the addList feature is for quotes: if you use the command “addlist quote” the bot will create an addquote command to allow your 
chat/moderation team to add quotes to a list. This would be done with a command like “addquote this guy can't kill me...whelp I just died -DFS
2021 (probably).” Lets say (to continue the example) that chat has captured 4 quotes; they would be able to use “quote 2” to pull the second 
quote on the list or”quote” (note the lack of number on this one) to pull a random quote from the list.

# Examples
----------
Here are some of my real life examples.  In this section I will be using Exclamation Points (!)
as my prefix.

For Basic Responses:
  me: !addcom cat I'm a kitty cat
  bot: Command Added
  me: !cat
  bot: I'm a kitty cat
  me: !editcom cat I'm a kitty cat, and I dance
  bot: Command Edited
  me !cat
  bot: I'm a kitty cat, and I dance

For Counters (here we can use the pound (#) symbol to replace a number with):
  me: !addcounter death You have have died # times
  bot: Command Added
  me: !death
  bot: You have died 1 times
  me: !editcom death You have died # time(s)
  bot: Command Edited
  me: !death
  bot: You have died 2 time(s)

For Lists (you can leave out the message or include it and the message will be the list name):
  example 1:
     me: !addlist note your #th note has been noted
     bot: List Added
     me: !addnote fix bot commands
     bot: your 1th note has been noted
     me: !note 1
     bot: note[1]: fix bot commands
     me: !note
     bot: note[1]: fix bot commands
  example 2:
     me: !addlist note
     bot: List Added
     me: !addnote fix bot commands
     bot: note
     me: !editcom addnote note added
     bot: Command Edited
     me: !addnote fix all the things
     bot: note added


# FAQ
------
Q: I added a new command into the GUI but it doesn't show up on the list.<br/>
A: Click the refresh button.  I haven't figured out how to get the interface to refresh automatically.

Q: I used an add command in Core Features on twitch but I didn't get a response.  Did the command work?<br/>
A: No, probably not.  Commands within the bot require a unique name, you may be running into a condition where you can't add the command because it already exists as a Counter or Basic Response.

Q: I double checked that the command doesn't exist in any other command tab but I still can't add the command.<br/>
A: The other issue that may occur is that the command has been removed or renamed within the Core Functions tab of commands.  Double check the command exists and that it is set to the proper function.

# Contact Us
-------------
Feel free to reach out with questions, comments, or support questions on my discord server under the #bot-programming channel.
If you question requires more in-depth support we can schedule a call on discord.
For lenghter questions our email address is DrunkFutureSelf@gmail.com.