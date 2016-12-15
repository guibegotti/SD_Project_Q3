// grab the things we need
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

// create a schema
var playerSchema = new Schema({
  name:{
    type: String,
    uppercase: true
 	},
  level: {type: Number, default: 1},
  hp: {type: Number, default: 50},
  maxhp: {type: Number, default: 50},
  exp: {type: Number, default: 0},
  expnext: {type: Number, default: 50},
  battling: {type: String, default: null},
  turn: {type: String, default: null}
});

// the schema is useless so far
// we need to create a model using it
var Player = mongoose.model('Player', playerSchema);

// make this available to our users in our Node applications
module.exports = Player;