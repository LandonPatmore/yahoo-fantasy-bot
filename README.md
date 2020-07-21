[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

### Feel free to contribute!

#### Current Roadmap

- [x] Kotlin (because why not :?)
- [x] Reactive X
- [ ] Docker Support
- [x] More in depth messages
- [ ] Respond to chat commands from various messaging services
- [ ] Clean up code

**Auto-deploys do not happen automatically. You will need to come back and click the "Deploy" button again to get the latest bot. It will reset everything. Just follow all the steps again and you will be good!**

# Yahoo Fantasy Bot
Bot that alerts GroupMe, Slack, and Discord users about various things happening in their Yahoo Fantasy Football League.
## What it does:
>Sends out messages at certain intervals: (All times are in UTC since it is not affected by DST. Will be sent at the correct times across timezones.)
>
>**Weekly Updates**
>| Type                                        | Weekday  | Time      |
>|---------------------------------------------|----------|-----------|
>| Weekly Matchups                             | Thursday | 23:30 UTC |
>| Score Update                                | Friday   | 03:55 UTC |
>| Score Update                                | Sunday   | 17:00 UTC |
>| Score Update                                | Sunday   | 20:00 UTC |
>| Score Update                                | Monday   | 00:00 UTC |
>| Score Update                                | Monday   | 03:55 UTC |
>| Close Score Update (matches within 15 pts)* | Monday   | 23:30 UTC |
>| Weekly Standings                            | Tuesday  | 16:30 UTC |
>| Score Update                                | Tuesday  | 03:55 UTC |
>
(\* If set to show Close Score Update)

>**League Transaction Alerts**
>* ADD
>* DROP
>* ADD/DROP
>* TRADE
>* COMMISH CHANGES
 
## Follow these steps EXACTLY!
1. Click the `Deploy to Heroku` button at the top. It will auto-deploy the application to Heroku.
2. Name the application whatever you would like.
3. Follow the below section.

---

### (REQUIRED) Setting up Yahoo API
You will need a Yahoo Access Token, Client ID, and Client Secret for this bot to work.

1. Go to https://developer.yahoo.com/apps/
2. Click "Create an App" button

![](https://imgur.com/VDgZ1Ze.png)

3. Fill out required information.
 * Name the application whatever you would like
 * Click "Installed Application"
 * The Redirect URI will be https://\<the name of your application\>.herokuapp.com/auth
 * Click "Fantasy Sports" and then "Read"
 * Click "Create App"
 
![](https://imgur.com/VqctUfM.png)

4. You will see your: Yahoo Client ID and Client Secret. Save these for later.

![](https://imgur.com/NbUwOmD.png)

5. To get your league ID: (2 Ways)

 **On the website**
 * Go to Yahoo Fantasy Football and click your league
 * Go to settings page
 * At the top, you will see "League ID", save for later
 
 **In the app**
 * Open the app
 * Click "League" tab
 * Click "Settings" at the top
 * At the top, you will see "League ID#", save for later

---

5. **The following sections are all optional, but at least one of them is needed. All of them can be used as well!**

(The following sections were taken from [dtcarls/ff_bot](https://github.com/dtcarls/ff_bot) as the steps are the exact same. They are tweaked a little bit.)

--- 

### (Optional) GroupMe Setup
<details>
 <summary>Click to expand</summary>
 <p>
 Go to www.groupme.com and sign up or login
 
 If you don't have one for your league already, create a new "Group Chat"
 
 ![](https://i.imgur.com/32ioDoZ.png)
 
 Next we will setup the bot for GroupMe
 
 Go to https://dev.groupme.com/session/new and login
 
 Click "Create Bot"
 
 ![](https://i.imgur.com/TI1bpwE.png)
 
 Create your bot. GroupMe does a good job explaining what each thing is.
 
 ![](https://i.imgur.com/DQUcuuI.png)
 
 After you have created your bot you will see something similar to this. Click "Edit"
 
 ![](https://i.imgur.com/Z9vwKKt.png)
 
 This page is important as you will need the "Bot ID" on this page.You can also send a test message with the text box to be sure it is connected to your chat room.
 Side note: If you use the bot id depicted in the page you will spam an empty chat room so not worth the effort
 
 ![](https://i.imgur.com/k65EZFJ.png)
 </p>
</details>
--- 

### (Optional) Slack setup
<details>
 <summary>Click to expand</summary>
 <p>
 Go to https://slack.com/signin and sign in to the workspace the bot will be in
 
 If you don't have one for your league already, create a new League Channel
 
 Next we will setup the bot for Slack
 
 Go to https://api.slack.com/apps/new
 
 Name the app, and choose the intended workspace from the dropdown.
 
 Select the Incoming Webhooks section on the side.
 
 ![](https://i.imgur.com/ziRQCVP.png)
 
 Change the toggle from Off to On.
 
 Select Add New Webhook to Workspace
 
 ![](https://i.imgur.com/tJRRrfz.png)
 
 In the Post to dropdown, select the channel you want to send messages to, then
 select Authorize.
 
 This page is important as you will need the "Webhook URL" on this page.
 
 ![](https://i.imgur.com/mmzhDS0.png)
 </p>
</details>
--- 

### (Optional) Discord setup
<details>
 <summary>Click to expand</summary>
 <p>
 Log into or create a discord account
 
 Go to or create a discord server to receive messages in
 
 Open the server settings
 
 ![](https://i.imgur.com/bDk2ttJ.png)
 
 Go to Webhooks
 
 ![](https://i.imgur.com/mfFHGbT.png)
 
 Create a webhook, give it a name and pick which channel to receive messages in
 
 ![](https://i.imgur.com/NAJLv6D.png)
 
 Save the "Webhook URL" on this page
 
 ![](https://i.imgur.com/U4MKZSY.png)
 </p>
</details>
--- 

6. Follow the below section.


### Heroku Setup

1. Go to your [dashboard](https://dashboard.heroku.com/apps). Now you will need to setup your environment variables so that it works for your league. Click Settings at your dashboard. Then click "Reveal Config Vars" button and you will see something like this.

![](https://imgur.com/8k1tZPs.png)

2. Fill out all the variables (You can have any combination of messaging services (0..n).)
3. Click "Deploy App". This will automatically configure the dynos and run all required scripts to create the bot.
4. Click "Overview"
5. Click "Configure Dynos" and turn on the "web" and "bot" dyno
6. Click "Open App" at the top right
7. Follow the setup and then close the window once it says "You are authorized".
8. Once you are authorized, Click "Configure Dynos" and turn OFF the "web" dyno (failing to do this will put your bot to sleep because of heroku policy, thus your bot will not function.)

### You are all set! Enjoy the bot!
