DROP TABLE IF EXISTS campaigns
CREATE TABLE campaigns AS SELECT csv.*, csv.Clicks::double / csv.Impressions as Ctr FROM 'csv/campaigns.csv' csv