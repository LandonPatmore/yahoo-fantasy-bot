/*
 * MIT License
 *
 * Copyright (c) 2020 Landon Patmore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.landonpatmore.yahoofantasybot.bot.utils

import com.landonpatmore.yahoofantasybot.bot.bridges.*
import com.landonpatmore.yahoofantasybot.bot.messaging.IMessagingService
import com.landonpatmore.yahoofantasybot.bot.messaging.Message
import com.landonpatmore.yahoofantasybot.bot.transformers.*
import com.landonpatmore.yahoofantasybot.shared.database.Db
import org.jsoup.Jsoup

class Arbiter(
//    private val dataRetriever: IDataRetriever,
    private val messageBridge: MessageBridge,
    private val jobRunner: JobRunner,
    private val transactionsBridge: TransactionsBridge,
    private val scoreUpdateBridge: ScoreUpdateBridge,
    private val closeScoreUpdateBridge: CloseScoreUpdateBridge,
    private val standingsBridge: StandingsBridge,
    private val matchUpBridge: MatchUpBridge,
    private val database: Db,
    private val messageServices: List<IMessagingService>
) {

    private fun setup() {
        setupTransactionsBridge()
        setupScoreUpdateBridge()
        setupCloseScoreUpdateBridge()
        setupMatchUpBridge()
        setupStandingsBridge()
//        setupJobs()
        setupMessageBridge()
//        sendInitialMessage()
    }

    fun start() {
        setup()

        val teamsdata = Jsoup.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fantasy_content xmlns=\"http://fantasysports.yahooapis.com/fantasy/v2/base.rng\" xmlns:yahoo=\"http://www.yahooapis.com/v1/base.rng\" xml:lang=\"en-US\" yahoo:uri=\"http://fantasysports.yahooapis.com/fantasy/v2/league/399.l.1116595/scoreboard\" time=\"252.34293937683ms\" copyright=\"Data provided by Yahoo! and STATS, LLC\" refresh_rate=\"60\">\n" +
                "  <league>\n" +
                "    <league_key>399.l.1116595</league_key>\n" +
                "    <league_id>1116595</league_id>\n" +
                "    <name>Yahoo Public 1116595</name>\n" +
                "    <url>https://football.fantasysports.yahoo.com/f1/1116595</url>\n" +
                "    <logo_url>https://s.yimg.com/cv/api/default/20180206/default-league-logo@2x.png</logo_url>\n" +
                "    <draft_status>postdraft</draft_status>\n" +
                "    <num_teams>10</num_teams>\n" +
                "    <edit_key>2</edit_key>\n" +
                "    <weekly_deadline />\n" +
                "    <league_update_timestamp>1600156929</league_update_timestamp>\n" +
                "    <scoring_type>head</scoring_type>\n" +
                "    <league_type>public</league_type>\n" +
                "    <renew />\n" +
                "    <renewed />\n" +
                "    <iris_group_chat_id />\n" +
                "    <allow_add_to_dl_extra_pos>0</allow_add_to_dl_extra_pos>\n" +
                "    <is_pro_league>0</is_pro_league>\n" +
                "    <is_cash_league>0</is_cash_league>\n" +
                "    <current_week>2</current_week>\n" +
                "    <start_week>1</start_week>\n" +
                "    <start_date>2020-09-10</start_date>\n" +
                "    <end_week>16</end_week>\n" +
                "    <end_date>2020-12-28</end_date>\n" +
                "    <game_code>nfl</game_code>\n" +
                "    <season>2020</season>\n" +
                "    <scoreboard>\n" +
                "      <week>2</week>\n" +
                "      <matchups count=\"5\">\n" +
                "        <matchup>\n" +
                "          <week>2</week>\n" +
                "          <week_start>2020-09-15</week_start>\n" +
                "          <week_end>2020-09-21</week_end>\n" +
                "          <status>midevent</status>\n" +
                "          <is_playoffs>0</is_playoffs>\n" +
                "          <is_consolation>0</is_consolation>\n" +
                "          <is_matchup_recap_available>0</is_matchup_recap_available>\n" +
                "          <teams count=\"2\">\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.1</team_key>\n" +
                "              <team_id>1</team_id>\n" +
                "              <name>Landon's Choice Team</name>\n" +
                "              <is_owned_by_current_login>1</is_owned_by_current_login>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/1</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_7_l.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>10</waiver_priority>\n" +
                "              <number_of_moves>3</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>5</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>B</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/1/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>1</manager_id>\n" +
                "                  <nickname>Landon</nickname>\n" +
                "                  <guid>Y4YNTNNZKW7T2XO6NXIJLPAF6C</guid>\n" +
                "                  <is_current_login>1</is_current_login>\n" +
                "                  <email>landon.patmore@gmail.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.58</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>119.85</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.3</team_key>\n" +
                "              <team_id>3</team_id>\n" +
                "              <name>T.Y. for the title</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/3</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://yahoofantasysports-res.cloudinary.com/image/upload/t_s192sq/fantasy-logos/56483905231_b9d379.jpg</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>8</waiver_priority>\n" +
                "              <number_of_moves>1</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>9</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>C</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/3/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>3</manager_id>\n" +
                "                  <nickname>Jon</nickname>\n" +
                "                  <guid>QRAWOJ2H6LUA3USHGOJYTLKRRU</guid>\n" +
                "                  <email>jdindy317@gmail.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/4733/38960487246_af732b_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.42</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>110.61</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "          </teams>\n" +
                "        </matchup>\n" +
                "        <matchup>\n" +
                "          <week>2</week>\n" +
                "          <week_start>2020-09-15</week_start>\n" +
                "          <week_end>2020-09-21</week_end>\n" +
                "          <status>midevent</status>\n" +
                "          <is_playoffs>0</is_playoffs>\n" +
                "          <is_consolation>0</is_consolation>\n" +
                "          <is_matchup_recap_available>0</is_matchup_recap_available>\n" +
                "          <teams count=\"2\">\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.2</team_key>\n" +
                "              <team_id>2</team_id>\n" +
                "              <name>Sara's Splendid Team</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/2</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_11_s.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>2</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>8</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>B-</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/2/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>2</manager_id>\n" +
                "                  <nickname>Sara</nickname>\n" +
                "                  <guid>DR5GFMXCZEGM7KHM2RCRA2VJQY</guid>\n" +
                "                  <email>skonger81@yahoo.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.45</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>117.68</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.10</team_key>\n" +
                "              <team_id>10</team_id>\n" +
                "              <name>Team 1</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/10</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_12.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>5</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>3</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>B</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/10/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>10</manager_id>\n" +
                "                  <nickname>-- hidden --</nickname>\n" +
                "                  <guid>6CLR2YPA6QNDVXJV4H3NLYVPMM</guid>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.55</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>123.19</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "          </teams>\n" +
                "        </matchup>\n" +
                "        <matchup>\n" +
                "          <week>2</week>\n" +
                "          <week_start>2020-09-15</week_start>\n" +
                "          <week_end>2020-09-21</week_end>\n" +
                "          <status>midevent</status>\n" +
                "          <is_playoffs>0</is_playoffs>\n" +
                "          <is_consolation>0</is_consolation>\n" +
                "          <is_matchup_recap_available>0</is_matchup_recap_available>\n" +
                "          <teams count=\"2\">\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.4</team_key>\n" +
                "              <team_id>4</team_id>\n" +
                "              <name>Brad's Team</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/4</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_3_b.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>6</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>2</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>C+</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/4/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>4</manager_id>\n" +
                "                  <nickname>Brad</nickname>\n" +
                "                  <guid>3UHANJBOE2W2OWKOILT3LMAGA4</guid>\n" +
                "                  <email>bradhunt615@yahoo.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.52</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>121.91</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.9</team_key>\n" +
                "              <team_id>9</team_id>\n" +
                "              <name>J</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/9</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_6_j.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>4</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>4</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>B</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/9/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>9</manager_id>\n" +
                "                  <nickname>JfromBk</nickname>\n" +
                "                  <guid>YU4XRTWABFPI6E5YOJ7DCXG5DM</guid>\n" +
                "                  <email>jheyfrombk@yahoo.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/4570/25070826258_537631_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.48</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>119.29</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "          </teams>\n" +
                "        </matchup>\n" +
                "        <matchup>\n" +
                "          <week>2</week>\n" +
                "          <week_start>2020-09-15</week_start>\n" +
                "          <week_end>2020-09-21</week_end>\n" +
                "          <status>midevent</status>\n" +
                "          <is_playoffs>0</is_playoffs>\n" +
                "          <is_consolation>0</is_consolation>\n" +
                "          <is_matchup_recap_available>0</is_matchup_recap_available>\n" +
                "          <teams count=\"2\">\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.5</team_key>\n" +
                "              <team_id>5</team_id>\n" +
                "              <name>Sedale's Swag Team</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/5</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_11_s.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>7</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>1</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>A-</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/5/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>5</manager_id>\n" +
                "                  <nickname>Sedale</nickname>\n" +
                "                  <guid>IJ3QUBX2JY47RWK5POH6GLXVRE</guid>\n" +
                "                  <email>lakersholmes24@icloud.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.5</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>124.34</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.8</team_key>\n" +
                "              <team_id>8</team_id>\n" +
                "              <name>Iggles3</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/8</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_10_j.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>1</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>10</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>B</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/8/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>8</manager_id>\n" +
                "                  <nickname>Joseph</nickname>\n" +
                "                  <guid>OFKGE2L4U5YWZKROY33G7NDFBY</guid>\n" +
                "                  <email>joelerch@yahoo.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/4576/38777829321_225e0a_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.5</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>124.77</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "          </teams>\n" +
                "        </matchup>\n" +
                "        <matchup>\n" +
                "          <week>2</week>\n" +
                "          <week_start>2020-09-15</week_start>\n" +
                "          <week_end>2020-09-21</week_end>\n" +
                "          <status>midevent</status>\n" +
                "          <is_playoffs>0</is_playoffs>\n" +
                "          <is_consolation>0</is_consolation>\n" +
                "          <is_matchup_recap_available>0</is_matchup_recap_available>\n" +
                "          <teams count=\"2\">\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.6</team_key>\n" +
                "              <team_id>6</team_id>\n" +
                "              <name>Robert's Team</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/6</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_12_r.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>9</waiver_priority>\n" +
                "              <number_of_moves>1</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>7</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>C</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/6/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>6</manager_id>\n" +
                "                  <nickname>Robert</nickname>\n" +
                "                  <guid>OHSB6HIV7HPLDZZ5FJIX4Z75EM</guid>\n" +
                "                  <email>bobbylpeskar@icloud.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.6</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>110.89</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "            <team>\n" +
                "              <team_key>399.l.1116595.t.7</team_key>\n" +
                "              <team_id>7</team_id>\n" +
                "              <name>TheBoneBone's Team</name>\n" +
                "              <url>https://football.fantasysports.yahoo.com/f1/1116595/7</url>\n" +
                "              <team_logos>\n" +
                "                <team_logo>\n" +
                "                  <size>large</size>\n" +
                "                  <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_10_t.png</url>\n" +
                "                </team_logo>\n" +
                "              </team_logos>\n" +
                "              <waiver_priority>3</waiver_priority>\n" +
                "              <number_of_moves>0</number_of_moves>\n" +
                "              <number_of_trades>0</number_of_trades>\n" +
                "              <roster_adds>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <coverage_value>2</coverage_value>\n" +
                "                <value>0</value>\n" +
                "              </roster_adds>\n" +
                "              <league_scoring_type>head</league_scoring_type>\n" +
                "              <draft_position>6</draft_position>\n" +
                "              <has_draft_grade>1</has_draft_grade>\n" +
                "              <draft_grade>C</draft_grade>\n" +
                "              <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/7/draftrecap</draft_recap_url>\n" +
                "              <managers>\n" +
                "                <manager>\n" +
                "                  <manager_id>7</manager_id>\n" +
                "                  <nickname>TheBoneBone</nickname>\n" +
                "                  <guid>FZ27K3Z5VEYRJHTGXPN5OOFT7Y</guid>\n" +
                "                  <email>braedonstogner@gmail.com</email>\n" +
                "                  <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "                </manager>\n" +
                "              </managers>\n" +
                "              <win_probability>0.4</win_probability>\n" +
                "              <team_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>0.00</total>\n" +
                "              </team_points>\n" +
                "              <team_projected_points>\n" +
                "                <coverage_type>week</coverage_type>\n" +
                "                <week>2</week>\n" +
                "                <total>100.48</total>\n" +
                "              </team_projected_points>\n" +
                "            </team>\n" +
                "          </teams>\n" +
                "        </matchup>\n" +
                "      </matchups>\n" +
                "    </scoreboard>\n" +
                "  </league>\n" +
                "</fantasy_content>")

//        closeScoreUpdateBridge.consumer.accept(teamsdata)
//        scoreUpdateBridge.consumer.accept(teamsdata)
//        matchUpBridge.consumer.accept(teamsdata)

        val transactions = Jsoup.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fantasy_content xmlns=\"http://fantasysports.yahooapis.com/fantasy/v2/base.rng\" xmlns:yahoo=\"http://www.yahooapis.com/v1/base.rng\" xml:lang=\"en-US\" yahoo:uri=\"http://fantasysports.yahooapis.com/fantasy/v2/league/399.l.1116595/transactions\" time=\"32.390832901001ms\" copyright=\"Data provided by Yahoo! and STATS, LLC\" refresh_rate=\"60\">\n" +
                "  <league>\n" +
                "    <league_key>399.l.1116595</league_key>\n" +
                "    <league_id>1116595</league_id>\n" +
                "    <name>Yahoo Public 1116595</name>\n" +
                "    <url>https://football.fantasysports.yahoo.com/f1/1116595</url>\n" +
                "    <logo_url>https://s.yimg.com/cv/api/default/20180206/default-league-logo@2x.png</logo_url>\n" +
                "    <draft_status>postdraft</draft_status>\n" +
                "    <num_teams>10</num_teams>\n" +
                "    <edit_key>2</edit_key>\n" +
                "    <weekly_deadline />\n" +
                "    <league_update_timestamp>1600156929</league_update_timestamp>\n" +
                "    <scoring_type>head</scoring_type>\n" +
                "    <league_type>public</league_type>\n" +
                "    <renew />\n" +
                "    <renewed />\n" +
                "    <iris_group_chat_id />\n" +
                "    <allow_add_to_dl_extra_pos>0</allow_add_to_dl_extra_pos>\n" +
                "    <is_pro_league>0</is_pro_league>\n" +
                "    <is_cash_league>0</is_cash_league>\n" +
                "    <current_week>2</current_week>\n" +
                "    <start_week>1</start_week>\n" +
                "    <start_date>2020-09-10</start_date>\n" +
                "    <end_week>16</end_week>\n" +
                "    <end_date>2020-12-28</end_date>\n" +
                "    <game_code>nfl</game_code>\n" +
                "    <season>2020</season>\n" +
                "    <transactions count=\"8\">\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.9</transaction_key>\n" +
                "        <transaction_id>9</transaction_id>\n" +
                "        <type>add</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1600013435</timestamp>\n" +
                "        <players count=\"1\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.26644</player_key>\n" +
                "            <player_id>26644</player_id>\n" +
                "            <name>\n" +
                "              <full>Tyler Eifert</full>\n" +
                "              <first>Tyler</first>\n" +
                "              <last>Eifert</last>\n" +
                "              <ascii_first>Tyler</ascii_first>\n" +
                "              <ascii_last>Eifert</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Jax</editorial_team_abbr>\n" +
                "            <display_position>TE</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>add</type>\n" +
                "              <source_type>freeagents</source_type>\n" +
                "              <destination_type>team</destination_type>\n" +
                "              <destination_team_key>399.l.1116595.t.1</destination_team_key>\n" +
                "              <destination_team_name>Landon's Choice Team</destination_team_name>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.8</transaction_key>\n" +
                "        <transaction_id>8</transaction_id>\n" +
                "        <type>add/drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599895775</timestamp>\n" +
                "        <players count=\"2\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.24830</player_key>\n" +
                "            <player_id>24830</player_id>\n" +
                "            <name>\n" +
                "              <full>Kyle Rudolph</full>\n" +
                "              <first>Kyle</first>\n" +
                "              <last>Rudolph</last>\n" +
                "              <ascii_first>Kyle</ascii_first>\n" +
                "              <ascii_last>Rudolph</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Min</editorial_team_abbr>\n" +
                "            <display_position>TE</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>add</type>\n" +
                "              <source_type>waivers</source_type>\n" +
                "              <destination_type>team</destination_type>\n" +
                "              <destination_team_key>399.l.1116595.t.1</destination_team_key>\n" +
                "              <destination_team_name>Landon's Choice Team</destination_team_name>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "          <player>\n" +
                "            <player_key>399.p.28592</player_key>\n" +
                "            <player_id>28592</player_id>\n" +
                "            <name>\n" +
                "              <full>Darren Waller</full>\n" +
                "              <first>Darren</first>\n" +
                "              <last>Waller</last>\n" +
                "              <ascii_first>Darren</ascii_first>\n" +
                "              <ascii_last>Waller</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>LV</editorial_team_abbr>\n" +
                "            <display_position>TE</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.1</source_team_key>\n" +
                "              <source_team_name>Landon's Choice Team</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.7</transaction_key>\n" +
                "        <transaction_id>7</transaction_id>\n" +
                "        <type>add</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599895775</timestamp>\n" +
                "        <players count=\"1\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.8826</player_key>\n" +
                "            <player_id>8826</player_id>\n" +
                "            <name>\n" +
                "              <full>DeSean Jackson</full>\n" +
                "              <first>DeSean</first>\n" +
                "              <last>Jackson</last>\n" +
                "              <ascii_first>DeSean</ascii_first>\n" +
                "              <ascii_last>Jackson</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Phi</editorial_team_abbr>\n" +
                "            <display_position>WR</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>add</type>\n" +
                "              <source_type>waivers</source_type>\n" +
                "              <destination_type>team</destination_type>\n" +
                "              <destination_team_key>399.l.1116595.t.1</destination_team_key>\n" +
                "              <destination_team_name>Landon's Choice Team</destination_team_name>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.6</transaction_key>\n" +
                "        <transaction_id>6</transaction_id>\n" +
                "        <type>add/drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599895775</timestamp>\n" +
                "        <players count=\"2\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.33138</player_key>\n" +
                "            <player_id>33138</player_id>\n" +
                "            <name>\n" +
                "              <full>James Robinson</full>\n" +
                "              <first>James</first>\n" +
                "              <last>Robinson</last>\n" +
                "              <ascii_first>James</ascii_first>\n" +
                "              <ascii_last>Robinson</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Jax</editorial_team_abbr>\n" +
                "            <display_position>RB</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>add</type>\n" +
                "              <source_type>waivers</source_type>\n" +
                "              <destination_type>team</destination_type>\n" +
                "              <destination_team_key>399.l.1116595.t.6</destination_team_key>\n" +
                "              <destination_team_name>Robert's Team</destination_team_name>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "          <player>\n" +
                "            <player_key>399.p.31905</player_key>\n" +
                "            <player_id>31905</player_id>\n" +
                "            <name>\n" +
                "              <full>David Montgomery</full>\n" +
                "              <first>David</first>\n" +
                "              <last>Montgomery</last>\n" +
                "              <ascii_first>David</ascii_first>\n" +
                "              <ascii_last>Montgomery</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Chi</editorial_team_abbr>\n" +
                "            <display_position>RB</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.6</source_team_key>\n" +
                "              <source_team_name>Robert's Team</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.5</transaction_key>\n" +
                "        <transaction_id>5</transaction_id>\n" +
                "        <type>add/drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599895775</timestamp>\n" +
                "        <players count=\"2\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.27120</player_key>\n" +
                "            <player_id>27120</player_id>\n" +
                "            <name>\n" +
                "              <full>Brandon McManus</full>\n" +
                "              <first>Brandon</first>\n" +
                "              <last>McManus</last>\n" +
                "              <ascii_first>Brandon</ascii_first>\n" +
                "              <ascii_last>McManus</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Den</editorial_team_abbr>\n" +
                "            <display_position>K</display_position>\n" +
                "            <position_type>K</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>add</type>\n" +
                "              <source_type>waivers</source_type>\n" +
                "              <destination_type>team</destination_type>\n" +
                "              <destination_team_key>399.l.1116595.t.3</destination_team_key>\n" +
                "              <destination_team_name>T.Y. for the title</destination_team_name>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "          <player>\n" +
                "            <player_key>399.p.3727</player_key>\n" +
                "            <player_id>3727</player_id>\n" +
                "            <name>\n" +
                "              <full>Adam Vinatieri</full>\n" +
                "              <first>Adam</first>\n" +
                "              <last>Vinatieri</last>\n" +
                "              <ascii_first>Adam</ascii_first>\n" +
                "              <ascii_last>Vinatieri</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Ind</editorial_team_abbr>\n" +
                "            <display_position>K</display_position>\n" +
                "            <position_type>K</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.3</source_team_key>\n" +
                "              <source_team_name>T.Y. for the title</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.4</transaction_key>\n" +
                "        <transaction_id>4</transaction_id>\n" +
                "        <type>drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599756801</timestamp>\n" +
                "        <players count=\"1\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.100014</player_key>\n" +
                "            <player_id>100014</player_id>\n" +
                "            <name>\n" +
                "              <full>Los Angeles</full>\n" +
                "              <first>Los Angeles</first>\n" +
                "              <last />\n" +
                "              <ascii_first>Los Angeles</ascii_first>\n" +
                "              <ascii_last />\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>LAR</editorial_team_abbr>\n" +
                "            <display_position>DEF</display_position>\n" +
                "            <position_type>DT</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.1</source_team_key>\n" +
                "              <source_team_name>Landon's Choice Team</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.3</transaction_key>\n" +
                "        <transaction_id>3</transaction_id>\n" +
                "        <type>drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599692550</timestamp>\n" +
                "        <players count=\"1\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.30784</player_key>\n" +
                "            <player_id>30784</player_id>\n" +
                "            <name>\n" +
                "              <full>Blake Jarwin</full>\n" +
                "              <first>Blake</first>\n" +
                "              <last>Jarwin</last>\n" +
                "              <ascii_first>Blake</ascii_first>\n" +
                "              <ascii_last>Jarwin</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Dal</editorial_team_abbr>\n" +
                "            <display_position>TE</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.1</source_team_key>\n" +
                "              <source_team_name>Landon's Choice Team</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "      <transaction>\n" +
                "        <transaction_key>399.l.1116595.tr.2</transaction_key>\n" +
                "        <transaction_id>2</transaction_id>\n" +
                "        <type>drop</type>\n" +
                "        <status>successful</status>\n" +
                "        <timestamp>1599686011</timestamp>\n" +
                "        <players count=\"1\">\n" +
                "          <player>\n" +
                "            <player_key>399.p.32756</player_key>\n" +
                "            <player_id>32756</player_id>\n" +
                "            <name>\n" +
                "              <full>Zack Moss</full>\n" +
                "              <first>Zack</first>\n" +
                "              <last>Moss</last>\n" +
                "              <ascii_first>Zack</ascii_first>\n" +
                "              <ascii_last>Moss</ascii_last>\n" +
                "            </name>\n" +
                "            <editorial_team_abbr>Buf</editorial_team_abbr>\n" +
                "            <display_position>RB</display_position>\n" +
                "            <position_type>O</position_type>\n" +
                "            <transaction_data>\n" +
                "              <type>drop</type>\n" +
                "              <source_type>team</source_type>\n" +
                "              <source_team_key>399.l.1116595.t.1</source_team_key>\n" +
                "              <source_team_name>Landon's Choice Team</source_team_name>\n" +
                "              <destination_type>waivers</destination_type>\n" +
                "            </transaction_data>\n" +
                "          </player>\n" +
                "        </players>\n" +
                "      </transaction>\n" +
                "    </transactions>\n" +
                "  </league>\n" +
                "</fantasy_content>")

        transactionsBridge.consumer.accept(Pair(1, transactions))

        val standings = Jsoup.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fantasy_content xmlns=\"http://fantasysports.yahooapis.com/fantasy/v2/base.rng\" xmlns:yahoo=\"http://www.yahooapis.com/v1/base.rng\" xml:lang=\"en-US\" yahoo:uri=\"http://fantasysports.yahooapis.com/fantasy/v2/league/399.l.1116595/standings\" time=\"330.14178276062ms\" copyright=\"Data provided by Yahoo! and STATS, LLC\" refresh_rate=\"60\">\n" +
                "  <league>\n" +
                "    <league_key>399.l.1116595</league_key>\n" +
                "    <league_id>1116595</league_id>\n" +
                "    <name>Yahoo Public 1116595</name>\n" +
                "    <url>https://football.fantasysports.yahoo.com/f1/1116595</url>\n" +
                "    <logo_url>https://s.yimg.com/cv/api/default/20180206/default-league-logo@2x.png</logo_url>\n" +
                "    <draft_status>postdraft</draft_status>\n" +
                "    <num_teams>10</num_teams>\n" +
                "    <edit_key>2</edit_key>\n" +
                "    <weekly_deadline />\n" +
                "    <league_update_timestamp>1600156929</league_update_timestamp>\n" +
                "    <scoring_type>head</scoring_type>\n" +
                "    <league_type>public</league_type>\n" +
                "    <renew />\n" +
                "    <renewed />\n" +
                "    <iris_group_chat_id />\n" +
                "    <allow_add_to_dl_extra_pos>0</allow_add_to_dl_extra_pos>\n" +
                "    <is_pro_league>0</is_pro_league>\n" +
                "    <is_cash_league>0</is_cash_league>\n" +
                "    <current_week>2</current_week>\n" +
                "    <start_week>1</start_week>\n" +
                "    <start_date>2020-09-10</start_date>\n" +
                "    <end_week>16</end_week>\n" +
                "    <end_date>2020-12-28</end_date>\n" +
                "    <game_code>nfl</game_code>\n" +
                "    <season>2020</season>\n" +
                "    <standings>\n" +
                "      <teams count=\"10\">\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.8</team_key>\n" +
                "          <team_id>8</team_id>\n" +
                "          <name>Iggles3</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/8</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_10_j.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>1</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>10</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>B</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/8/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>8</manager_id>\n" +
                "              <nickname>Joseph</nickname>\n" +
                "              <guid>OFKGE2L4U5YWZKROY33G7NDFBY</guid>\n" +
                "              <email>joelerch@yahoo.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/4576/38777829321_225e0a_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>163.88</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>1</rank>\n" +
                "            <playoff_seed>1</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>1</wins>\n" +
                "              <losses>0</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>1.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>win</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>163.88</points_for>\n" +
                "            <points_against>94.84</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.2</team_key>\n" +
                "          <team_id>2</team_id>\n" +
                "          <name>Sara's Splendid Team</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/2</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_11_s.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>2</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>8</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>B-</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/2/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>2</manager_id>\n" +
                "              <nickname>Sara</nickname>\n" +
                "              <guid>DR5GFMXCZEGM7KHM2RCRA2VJQY</guid>\n" +
                "              <email>skonger81@yahoo.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>134.86</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>2</rank>\n" +
                "            <playoff_seed>2</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>1</wins>\n" +
                "              <losses>0</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>1.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>win</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>134.86</points_for>\n" +
                "            <points_against>91.18</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.10</team_key>\n" +
                "          <team_id>10</team_id>\n" +
                "          <name>Team 1</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/10</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_12.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>5</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>3</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>B</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/10/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>10</manager_id>\n" +
                "              <nickname>-- hidden --</nickname>\n" +
                "              <guid>6CLR2YPA6QNDVXJV4H3NLYVPMM</guid>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>132.00</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>3</rank>\n" +
                "            <playoff_seed>3</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>1</wins>\n" +
                "              <losses>0</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>1.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>win</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>132.00</points_for>\n" +
                "            <points_against>129.74</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.7</team_key>\n" +
                "          <team_id>7</team_id>\n" +
                "          <name>TheBoneBone's Team</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/7</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_10_t.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>3</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>6</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>C</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/7/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>7</manager_id>\n" +
                "              <nickname>TheBoneBone</nickname>\n" +
                "              <guid>FZ27K3Z5VEYRJHTGXPN5OOFT7Y</guid>\n" +
                "              <email>braedonstogner@gmail.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>123.90</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>4</rank>\n" +
                "            <playoff_seed>4</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>1</wins>\n" +
                "              <losses>0</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>1.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>win</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>123.90</points_for>\n" +
                "            <points_against>116.22</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.9</team_key>\n" +
                "          <team_id>9</team_id>\n" +
                "          <name>J</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/9</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_6_j.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>4</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>4</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>B</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/9/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>9</manager_id>\n" +
                "              <nickname>JfromBk</nickname>\n" +
                "              <guid>YU4XRTWABFPI6E5YOJ7DCXG5DM</guid>\n" +
                "              <email>jheyfrombk@yahoo.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/4570/25070826258_537631_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>98.10</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>5</rank>\n" +
                "            <playoff_seed>5</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>1</wins>\n" +
                "              <losses>0</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>1.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>win</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>98.10</points_for>\n" +
                "            <points_against>85.9</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.6</team_key>\n" +
                "          <team_id>6</team_id>\n" +
                "          <name>Robert's Team</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/6</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_12_r.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>9</waiver_priority>\n" +
                "          <number_of_moves>1</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>7</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>C</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/6/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>6</manager_id>\n" +
                "              <nickname>Robert</nickname>\n" +
                "              <guid>OHSB6HIV7HPLDZZ5FJIX4Z75EM</guid>\n" +
                "              <email>bobbylpeskar@icloud.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>129.74</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>6</rank>\n" +
                "            <playoff_seed>6</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>0</wins>\n" +
                "              <losses>1</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>loss</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>129.74</points_for>\n" +
                "            <points_against>132</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.5</team_key>\n" +
                "          <team_id>5</team_id>\n" +
                "          <name>Sedale's Swag Team</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/5</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_11_s.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>7</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>1</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>A-</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/5/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>5</manager_id>\n" +
                "              <nickname>Sedale</nickname>\n" +
                "              <guid>IJ3QUBX2JY47RWK5POH6GLXVRE</guid>\n" +
                "              <email>lakersholmes24@icloud.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>116.22</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>7</rank>\n" +
                "            <playoff_seed>7</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>0</wins>\n" +
                "              <losses>1</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>loss</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>116.22</points_for>\n" +
                "            <points_against>123.9</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.4</team_key>\n" +
                "          <team_id>4</team_id>\n" +
                "          <name>Brad's Team</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/4</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_3_b.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>6</waiver_priority>\n" +
                "          <number_of_moves>0</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>2</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>C+</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/4/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>4</manager_id>\n" +
                "              <nickname>Brad</nickname>\n" +
                "              <guid>3UHANJBOE2W2OWKOILT3LMAGA4</guid>\n" +
                "              <email>bradhunt615@yahoo.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>94.84</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>8</rank>\n" +
                "            <playoff_seed>8</playoff_seed>\n" +
                "            <outcome_totals>\n" +
                "              <wins>0</wins>\n" +
                "              <losses>1</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>loss</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>94.84</points_for>\n" +
                "            <points_against>163.88</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.1</team_key>\n" +
                "          <team_id>1</team_id>\n" +
                "          <name>Landon's Choice Team</name>\n" +
                "          <is_owned_by_current_login>1</is_owned_by_current_login>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/1</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://s.yimg.com/cv/apiv2/default/nfl/nfl_7_l.png</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>10</waiver_priority>\n" +
                "          <number_of_moves>3</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>5</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>B</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/1/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>1</manager_id>\n" +
                "              <nickname>Landon</nickname>\n" +
                "              <guid>Y4YNTNNZKW7T2XO6NXIJLPAF6C</guid>\n" +
                "              <is_current_login>1</is_current_login>\n" +
                "              <email>landon.patmore@gmail.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/default_user_profile_pic_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>91.18</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>9</rank>\n" +
                "            <outcome_totals>\n" +
                "              <wins>0</wins>\n" +
                "              <losses>1</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>loss</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>91.18</points_for>\n" +
                "            <points_against>134.86</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "        <team>\n" +
                "          <team_key>399.l.1116595.t.3</team_key>\n" +
                "          <team_id>3</team_id>\n" +
                "          <name>T.Y. for the title</name>\n" +
                "          <url>https://football.fantasysports.yahoo.com/f1/1116595/3</url>\n" +
                "          <team_logos>\n" +
                "            <team_logo>\n" +
                "              <size>large</size>\n" +
                "              <url>https://yahoofantasysports-res.cloudinary.com/image/upload/t_s192sq/fantasy-logos/56483905231_b9d379.jpg</url>\n" +
                "            </team_logo>\n" +
                "          </team_logos>\n" +
                "          <waiver_priority>8</waiver_priority>\n" +
                "          <number_of_moves>1</number_of_moves>\n" +
                "          <number_of_trades>0</number_of_trades>\n" +
                "          <roster_adds>\n" +
                "            <coverage_type>week</coverage_type>\n" +
                "            <coverage_value>2</coverage_value>\n" +
                "            <value>0</value>\n" +
                "          </roster_adds>\n" +
                "          <league_scoring_type>head</league_scoring_type>\n" +
                "          <draft_position>9</draft_position>\n" +
                "          <has_draft_grade>1</has_draft_grade>\n" +
                "          <draft_grade>C</draft_grade>\n" +
                "          <draft_recap_url>https://football.fantasysports.yahoo.com/f1/1116595/3/draftrecap</draft_recap_url>\n" +
                "          <managers>\n" +
                "            <manager>\n" +
                "              <manager_id>3</manager_id>\n" +
                "              <nickname>Jon</nickname>\n" +
                "              <guid>QRAWOJ2H6LUA3USHGOJYTLKRRU</guid>\n" +
                "              <email>jdindy317@gmail.com</email>\n" +
                "              <image_url>https://s.yimg.com/ag/images/4733/38960487246_af732b_64sq.jpg</image_url>\n" +
                "            </manager>\n" +
                "          </managers>\n" +
                "          <team_points>\n" +
                "            <coverage_type>season</coverage_type>\n" +
                "            <season>2020</season>\n" +
                "            <total>85.90</total>\n" +
                "          </team_points>\n" +
                "          <team_standings>\n" +
                "            <rank>10</rank>\n" +
                "            <outcome_totals>\n" +
                "              <wins>0</wins>\n" +
                "              <losses>1</losses>\n" +
                "              <ties>0</ties>\n" +
                "              <percentage>.000</percentage>\n" +
                "            </outcome_totals>\n" +
                "            <streak>\n" +
                "              <type>loss</type>\n" +
                "              <value>1</value>\n" +
                "            </streak>\n" +
                "            <points_for>85.90</points_for>\n" +
                "            <points_against>98.1</points_against>\n" +
                "          </team_standings>\n" +
                "        </team>\n" +
                "      </teams>\n" +
                "    </standings>\n" +
                "  </league>\n" +
                "</fantasy_content>\n" +
                "  <!-- fantasy-sports-api- -public-production-bf1-76c886cc56-n5j7d Wed Sep 16 04:35:43 UTC 2020 -->")

//        standingsBridge.consumer.accept(standings)
//        Observable.interval(0, 15, TimeUnit.SECONDS)
//            .subscribe {
//                try {
//                    val event =
//                        dataRetriever.yahooApiRequest(YahooApiRequest.Transactions)
//                    val latestTimeChecked = database.getLatestTimeChecked()
//                    transactionsBridge.consumer.accept(
//                        Pair(
//                            latestTimeChecked,
//                            event
//                        )
//                    )
//                    database.saveLatestTimeChecked(System.currentTimeMillis())
//                } catch (e: Exception) {
//                    println(e.message)
//                }
//            }
    }

    private fun sendInitialMessage() {
        if (!database.wasStartupMessageReceived()) {
            messageBridge.consumer.accept(
                Message.Generic(
                    """
                        |Thanks for using me!  I will notify you about things happening in your league in real time!
                        |Star/fork me on Github: https://github.com/LandonPatmore/yahoo-fantasy-bot
                        |Having issues?: https://github.com/LandonPatmore/yahoo-fantasy-bot/issues
                    """.trimMargin()
                )
            )
            database.startupMessageReceived()
        } else {
            println("Start up message already sent, not sending...")
        }
    }

    private fun setupTransactionsBridge() {
        val transactions = transactionsBridge.eventStream
            .convertToTransactionMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupScoreUpdateBridge() {
        val transactions = scoreUpdateBridge.eventStream
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupCloseScoreUpdateBridge() {
        val transactions = closeScoreUpdateBridge.eventStream
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage(true)

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupMatchUpBridge() {
        val transactions = matchUpBridge.eventStream
            .convertToMatchUpObject()
            .convertToMatchUpMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupStandingsBridge() {
        val standings = standingsBridge.eventStream
            .convertToStandingsMessage()

        standings.subscribe(messageBridge.consumer)
    }

    private fun setupMessageBridge() {
        val messages = messageBridge.eventStream
            .convertToMessageInfo()

        messageServices.forEach {
            messages.subscribe(it)
        }
    }

//    private fun setupJobs() {
//        // Times are in UTC since it is not effected by DST
//        if (TODO()) {
//            jobRunner.createJob(
//                CloseScoreUpdateJob::class.java,
//                "0 30 23 ? 9-1 MON *"
//            )
//        }
//
//        jobRunner.createJob(MatchUpJob::class.java, "0 30 23 ? 9-1 THU *")
//        jobRunner.createJob(StandingsJob::class.java, "0 30 16 ? 9-1 TUE *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 FRI *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 17 ? 9-1 SUN *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 20 ? 9-1 SUN *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 0 ? 9-1 MON *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 MON *")
//        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 TUE *")
//
//        jobRunner.runJobs()
//    }
}
