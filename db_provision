CREATE TABLE latest_time (
    latest_time bigint PRIMARY KEY
);

CREATE UNIQUE INDEX lt_pkey ON latest_time(latest_time int8_ops);

-----------------------------------------------------------------------------------------------------------

CREATE TABLE start_up_message_received (
    was_received boolean PRIMARY KEY
);

CREATE UNIQUE INDEX sumr_pkey ON start_up_message_received(was_received bool_ops);

-----------------------------------------------------------------------------------------------------------

CREATE TABLE tokens (
    "yahooRefreshToken" text,
    "yahooTokenRetrievedTime" bigint PRIMARY KEY,
    "yahooTokenRawResponse" text,
    "yahooTokenType" text,
    "yahooAccessToken" text,
    "yahooTokenExpireTime" bigint,
    "yahooTokenScope" text
);

CREATE UNIQUE INDEX t_pkey ON tokens("yahooTokenRetrievedTime" int8_ops);