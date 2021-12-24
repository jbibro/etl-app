# etl-app

# How to run


```
./gradlew bootRun
```

Alternatively application can be run on docker

- Build image to local Docker daemon
```
./gradlew jibDockerBuild --image etl
```
- Start containers
```
docker run -p 8080:8080 etl
```

## API
> Metrics: clicks, impressions, ctr
> 
> Dimensions: daily, datasource, campaign


/analyze 

| Query params | Description |
| --- | --- |
| min | get minimum value for selected metrics, e.g. `min=clicks,ctr`  |
| max | get maximum value for selected metrics, e.g. `max=impressinons,ctr`  |
| sum | get total value for selected metrics, e.g. `sum=clicks,ctr,impressions`  |
| avg | get average value for selected metrics, e.g. `avg=clicks,ctr`  |
| groupBy | group by selected dimensions, e.g. `groupBy=datasource,campaign`  |
| filters | list of dimension filters in the following format: `field.operator.value`, e.g. `Daily.le.2019-12-05`. Possible operators are `eq`, `le` and `ge`   |

## Example

### Get the highest clicks, impressions and average ctr grouped by Datasource between 2019-12-01 and 2019-12-03
```javascript
GET /analyze?max=clicks,impressions&avg=ctr&groupBy=Datasource&filters=Daily.ge.2019-12-01&filters=Daily.le.2019-12-30
[
    {
        "max clicks": 256,
        "max impressions": 64961,
        "avg ctr": 0.013742478165406703,
        "Datasource": "Google Ads"
    },
    {
        "max clicks": 6989,
        "max impressions": 18245,
        "avg ctr": 0.11049091369871529,
        "Datasource": "Twitter Ads"
    },
    {
        "max clicks": 276,
        "max impressions": 15311,
        "avg ctr": 0.048903110975291174,
        "Datasource": "Facebook Ads"
    }
]
```

