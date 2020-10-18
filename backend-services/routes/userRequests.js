var express = require('express');
var router = express.Router();
const { Connection, Request } = require("tedious");

const config = {
    authentication: {
        options: {
            userName: "tom", // update me
            password: "Password@0" // update me
        },
        type: "default"
    },
    server: "8resq.database.windows.net", // update me
    options: {
        database: "8ResQ", //update me
        encrypt: true
    }
};

router.post('/raise', function(request, response) {
    //console.log(request.body);

    const connection = new Connection(config);
    connection.on("connect", err => {
        if (err) {
            console.error(err.message);
        } else {
            console.log("CONNECTED!");
            queryDatabaseToInsert(request);
        }
    });
    function queryDatabaseToInsert(request) {
        //console.log("Reading rows from the Table...");
        console.log(request.body);
        // console.log('addressedBy' in request.body);
        const addressedBy = request.body.addressedBy || ""; 
        const comments = request.body.comments || "";
        const completed = request.body.completed || "";
        const description = request.body.description || "";
        const priority = request.body.priority || "";
        const quantity = request.body.quantity || "";
        const requestedOn = request.body.requestedOn || "";
        const title = request.body.title || "";
        const type = request.body.type || "";
        const userId = request.body.userId || "";
        
        sql = "INSERT INTO requests (addressedBy, comments, completed, description, priority, quantity, requestedOn, title, type, userId) VALUES('" 
        + addressedBy + "','" + comments + "','" + completed + "','" + description + "','" + priority + "','" + quantity + "','" + requestedOn + "','" + title + "','" + type + "','" + userId + "');";

        console.log(sql);
        const req = new Request(sql,
            (err, rowCount) => {
              if (err) {
                console.error(err.message);
                response.status(400).send({"message": "Insert Unsuccessful", "status": 400});
              } else {
                response.status(200).send({"message": "Insert Successful", "status": 200});
              }
            }
          );
    connection.execSql(req);
  }    
    
});

router.post('/getRequestsForUser', function(context, response) {
    var queryResult = [];
    const connection = new Connection(config);
    connection.on("connect", err => {
        if (err) {
            console.error(err.message);
        } else {
            console.log("CONNECTED!");
            getAllRequests(context.body.userId || "");
        }
    });

    function getAllRequests(id) {
        //console.log("Reading rows from the Table...");
      
        // Read all rows from table
        console.log(id);
        if(id != '')
            sql = `SELECT * FROM requests WHERE userId='`+ id + `';`;
        else
            sql = `SELECT * FROM requests;`;
        console.log(sql);
  
        // Read all rows from table
        const request = new Request(sql,
        (err, rowCount) => {
            if (err) {
            console.error(err.message);
            } else {
            //console.log(`${rowCount} row(s) returned`);
            }
        }
        );
    
        request.on("row", columns => {
            var rowVal = {};
        columns.forEach(column => {
            //console.log("%s\t%s", column.metadata.colName, column.value);
            rowVal[column.metadata.colName] = column.value;
            // console.log(rowVal);
        });
        queryResult.push(rowVal);
        //console.log(queryResult);
        });
        
        request.on('requestCompleted', function() {
        context.res = {
            status: 200,
            body: queryResult
        };

        //context.done();
            response.status(200).send(queryResult);
        })

        connection.execSql(request);
    }
});

router.post('/updateRequest', function(request, response) {
    //console.log(request.body);
    const connection = new Connection(config);
    connection.on("connect", err => {
        if (err) {
            console.error(err.message);
        } else {
            console.log("CONNECTED!");
            queryDatabaseToUpdate(request);
        }
    });
    function queryDatabaseToUpdate(request) {
        //console.log("Reading rows from the Table...");
        console.log(request.body);
        // console.log('addressedBy' in request.body);
        const id = request.body.id || "";
        const addressedBy = request.body.addressedBy || ""; 
        const comments = request.body.comments || "";
        const completed = request.body.completed || "";
        const description = request.body.description || "";
        const priority = request.body.priority || "";
        const quantity = request.body.quantity || "";
        const requestedOn = request.body.requestedOn || "";
        const title = request.body.title || "";
        const type = request.body.type || "";
        
        sql = "UPDATE requests SET addressedBy='" + addressedBy + "', comments='" + comments + "', completed='" + completed + "', description='" + description + "', priority='" + priority + "', quantity='" + quantity + "', requestedOn='" + requestedOn + "', title='" + title + "', type='" + type + "' WHERE id='" + id + "';";

        console.log(sql);
        const req = new Request(sql,
            (err, rowCount) => {
              if (err) {
                console.error(err.message);
                response.status(400).send({"message": "Update Unsuccessful", "status": 400});
              } else {
                response.status(200).send({"message": "Update Successful", "status": 200});
              }
            }
          );
    connection.execSql(req);
  }    
    
});

router.post('/closeRequest', function(request, response) {
    //console.log(request.body);
    const connection = new Connection(config);
    connection.on("connect", err => {
        if (err) {
            console.error(err.message);
        } else {
            console.log("CONNECTED!");
            queryDatabaseToClose(request);
        }
    });
    function queryDatabaseToClose(request) {
        //console.log("Reading rows from the Table...");
        console.log(request.body);
        // console.log('addressedBy' in request.body);
        
        sql = "UPDATE requests SET completed='true' WHERE id='" + request.body.id + "';";

        console.log(sql);
        const req = new Request(sql,
            (err, rowCount) => {
              if (err) {
                console.error(err.message);
                response.status(400).send({"message": "Close Unsuccessful", "status": 400});
              } else {
                response.status(200).send({"message": "Close Successful", "status": 200});
              }
            }
          );
    connection.execSql(req);
  }    
    
});


module.exports = router;