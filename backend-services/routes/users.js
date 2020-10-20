var express = require('express');
var router = express.Router();
const { Connection, Request } = require("tedious");

// Create connection to database
const config = {
  authentication: {
    options: {
      userName: "", // update me
      password: "" // update me
    },
    type: "default"
  },
  server: "", // update me
  options: {
    database: "", //update me
    encrypt: true
  }
};
/* GET users listing. */
router.get('/', function(context, req) {
  var queryResult = [];
  var res = {};
  
  const connection = new Connection(config);
  connection.on("connect", err => {
    if (err) {
      console.error(err.message);
    } else {
      console.log("CONNECTED!");
      queryDatabase();
    }
  });

  function queryDatabase() {
    console.log("Reading rows from the Table...");
  
    // Read all rows from table
    const request = new Request(
      `SELECT * FROM users`,
      (err, rowCount) => {
        if (err) {
          console.error(err.message);
        } else {
          console.log(`${rowCount} row(s) returned`);
        }
      }
    );
  
    request.on("row", columns => {
      var rowVal = {}
      columns.forEach(column => {
        //console.log("%s\t%s", column.metadata.colName, column.value);
        rowVal[column.metadata.colName] = column.value;
        // console.log(rowVal);
      });
      queryResult.push(rowVal);
      console.log(queryResult);
    });
    
    request.on('requestCompleted', function() {
      context.res = {
        status: 200,
        body: queryResult
      };

      //context.done();
      req.send(queryResult);
    });

    connection.execSql(request);
  }
});

router.post('/active', function(context, response) {
  //console.log(request.body);
  var queryResult = {};
  const connection = new Connection(config);
  connection.on("connect", err => {
      if (err) {
          console.error(err.message);
      } else {
          console.log("CONNECTED!");
          checkUser(context.body.id);
      }
  });
  function checkUser(id) {
      //console.log("Reading rows from the Table...");
      // console.log('addressedBy' in request.body);
      
      sql = "SELECT * FROM users WHERE id='" + id + "';";

      console.log(sql);
      const req = new Request(sql,
          (err, rowCount) => {
            if (err) {
              console.error(err.message);
              response.status(400).send({"message": "Invalid User Id", "status": 400});
            }
            else if(rowCount == 0) {
              response.status(400).send({"message": "Invalid User Id", "status": 400});
            }
          }
        );

        req.on("row", columns => {
          var id = "";
          var isActive = 0;
          columns.forEach(column => {
              if (column.metadata.colName == 'id')
                  id = column.value;
              else if (column.metadata.colName == 'isActive')
                  isActive = column.value;
          });
          queryResult = {"id": id, "isActive": isActive};
        });
        
        req.on('requestCompleted', function() {
          context.res = {
            status: 200,
            body: queryResult
          };
    
          //context.done();
          response.status(200).send(queryResult);
        });
  connection.execSql(req);
}    
  
});

module.exports = router;
