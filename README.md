# GoT Quotes microservice in Java 
    
## Description

A very simple GoT quotes service example in Java.  

## Endpoints

### Get quote by id

Get quote by numeric id.

```
GET /api/quote/<quoteid>
```

Response:

```
{
	"quote": STRING,
	"counter": INTEGER
}
```

### Get random quote

Get random quote.

```
GET /api/quote/random
```

Response:

```
{
	"quote": STRING,
	"counter": INTEGER
}
```

### Get top quote (most returned)

Get most resturned quote.

```
GET /api/quote/top
```

Response:

```
{
	"quote": STRING,
	"counter": INTEGER
}
```

### Error response

Response returned in case the requested quote doesn't exist.

Response:

```
{
	"status": 404,
	"message": "Quote not found!"
}
```
