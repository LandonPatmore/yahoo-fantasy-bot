# yahoo-trader-bot
Bot that alerts GroupMe users about various things happening in their Yahoo Fantasy Sports league.

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

## Follow these steps EXACTLY!
1. Click the above button.  It will auto-deploy the application to Heroku.
2. Name the application whatever you would like.
3. When it asks for environment variables at first, you do not need to put any in that are not already filled in.  These will be added later.
4. Click "Deploy App".  This will automatically configure the dynos and run all required scripts to get the DB working.

5. Follow the below section.

### Setting up Yahoo API
You will need a Yahoo Access Token, Client ID, and Client Secret for this bot to work.

Steps to retrieve these variables:

1. Go to https://developer.yahoo.com/apps/
2. Click "Create an App" button

![](https://imgur.com/VDgZ1Ze.png)

3. Fill out required information.
  * Name the application whatever you would like.
  * Click "Installed Application"
  * The callback domain name will be <HEROKU APP NAME>.herokuapp.com (Do not put http:// or https://)
  * Click "Fantasy Sports" and then "Read"
  * Click "Create App"
  
![](https://imgur.com/VqctUfM.png)

4. You will see your: Yahoo Client ID and Client Secret.  Save these for later.

![](https://imgur.com/NbUwOmD.png)

