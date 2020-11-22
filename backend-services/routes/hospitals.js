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
/* GET hospitals listing for a county. */
router.post('/getHospitals', function(context, req) {
  var rowVal = {};
  var queryResult = [];
  const hospitals = ["Emory", "Anchor", "Grady", "Georgia Hospital", "Lakeview", "Peachford", 
                        "Piedmont", "Ridgeview", "Riverwoods", "Tanner", "Wellstar", "Youth villages",
                        "AdventHealth Gordon", "AdventHealth Murray", "Fairview Park Hospital", "Northeast Georgia Medical Center Lumpkin",
                         "Putnam General Hospital", "St. Mary's Hospital", "University Hospital McDuffie", "WellStar West Georgia Medical Center", 
                        "Wills Memorial Hospital", "WellStar Cobb Hospital", "University Hospital", "South Georgia Medical Center Berrien",
                        "Optim Medical Center - Tattnall", "Northridge Medical Center"
                    ]
  var res = {};
  
  const connection = new Connection(config);
  connection.on("connect", err => {
    if (err) {
      console.error(err.message);
    } else {
      console.log("CONNECTED!", context);
      queryDatabase(context.body.county);
    }
  });

  function queryDatabase(county) {
    console.log("Reading rows from the Table...");
    const sql = "SELECT * FROM hospital_beds_USA_v1 WHERE county='" + county + "';";
    console.log(sql);
    // Read all rows from table
    const request = new Request(
      sql,
      (err, rowCount) => {
        if (err) {
          console.error(err.message);
        } else {
          console.log(`${rowCount} row(s) returned`);
        }
      }
    );

    function randomNumber(min, max) {  
        return Math.floor(Math.random() * (max - min) + min); 
    }
    
    function hospitalName() {
        return hospitals[Math.floor(Math.random() * hospitals.length)];
    }
  
    request.on("row", columns => {
        var rowVal = {};
      columns.forEach(column => {
        //console.log("%s\t%s", column.metadata.colName, column.value);
        var columnName = column.metadata.colName;
        if (columnName == 'beds') {
            rowVal[columnName] = parseInt(column.value * 100);    
        }
        else
            rowVal[columnName] = column.value;
        // console.log(rowVal);
      });
      var costValue = randomNumber(1000, 10000);
      rowVal['cost'] = costValue;
      rowVal['hospitalName'] = hospitalName();
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

module.exports = router;
