# yahoo-trader-bot
Bot that alerts GroupMe users about various things happening in their Yahoo Fantasy Sports league.

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

### Setting up Yahoo API
You will need a Yahoo Access Token, Client ID, and Client Secret for this bot to work.

Steps to retrieve these variables:

1. Go to https://developer.yahoo.com/apps/
2. Click "Create an App" button
3. Fill out required information, check the box that says "Installed Application" (THIS IS IMPORTANT), check the box that says "Fantasy Sports", and check the box that says "Read".  The click "Create App"
4. At the top of the next page, you will see your: Yahoo Access Token, Client ID, and Client Secret.  Save these for later.

### GroupMe Setup
(This section was taken from [dtcarls/ff_bot](https://github.com/dtcarls/ff_bot) as it is the exact same for this bot's purposes.)

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

This page is important as you will need the "Bot ID" and "Group Id" on this page. You can also send a test message with the text box to be sure it is connected to your chat room.
Side note: If you use the bot id depicted in the page you will spam an empty chat room, so not worth the effort.

![](https://i.imgur.com/k65EZFJ.png)

### Setting up Heroku
[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

Just click the above button and you will be brought to a page that lets you set up the bot.  All of the variables you got from GroupMe and Yahoo are now going to be placed in the text boxes.  Remember to put the right keys in the right places, or the bot will not work.

![](https://imgur.com/VTMxvGU.png)

Click "Deploy App" and your bot will be set up.

(This section was also taken from [dtcarls/ff_bot](https://github.com/dtcarls/ff_bot) as it is the exact same for this bot's purposes.)

After you have setup your variables you will need to turn it on. Navigate to the "Resources" tab of your Heroku app Dashboard.
You should see something like below. Click the pencil on the right and toggle the buton so it is blue like depicted and click "Confirm."

![](https://i.imgur.com/J6bpV2I.png)

You're done! You now have a fully featured GroupMe/Slack/Discord chat bot for ESPN leagues! If you have an INIT_MSG you will see it exclaimed in your GroupMe, Discord, or Slack chat room.

Unfortunately to do auto deploys of the latest version you need admin access to the repository on git. You can check for updates on the github page (https://github.com/dtcarls/ff_bot/commits/master) and click the deploy button again; however, this will deploy a new instance and the variables will need to be edited again.
