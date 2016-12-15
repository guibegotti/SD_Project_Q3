var express = require('express');
var session = require('express-session');
var bodyParser= require('body-parser');

var mongoose = require('mongoose');
var ejs = require('ejs');

var app = express();

// var http = require('http').Server(app);
var io = require('socket.io')(app.listen(3005, () => {
    	console.log('listening on 3005');
  	}));

app.set('view engine', 'ejs');

app.use(session({secret: 'secret'}));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(express.static('public'));


mongoose.connect('mongodb://testuser:testpass@ds133328.mlab.com:33328/projetosd', function(err) {
    if (err) throw err;
});

var Player = require('./models/player.js');

io.on('connection', function(socket){
  // console.log('a user connected to a socket');
});

var sess;


app.get('/', (req, res) => {
	if(req.session.name)
	res.redirect('/players');
	else
  	Player.find(function(err, results) {
    	res.render("create.ejs") ;
  	});
});

app.get('/logout', (req, res) => {
	req.session.destroy(function(err) {
 		if(err) {
   		 console.log(err);
  		} else {
    	res.redirect('/');
  		}
  	});
});

app.get('/players', (req, res) => {	

  	if(req.session.name)
  	Player.find(function(err, results) {
  		Player.findOne({name: (req.session.name).toUpperCase()}, function(error, player) {
  			req.session.battling = player.battling;
  			req.session.turn = player.battling;
  			req.session.level = player.level;
			req.session.hp = player.hp;	
			req.session.maxhp = player.maxhp;
  			res.render("players.ejs" , {players: results, session: req.session}) ;
  		});
  	});
	else
	res.redirect('/');	
});



app.get('/battle/:name', (req, res) => {
	var plist = new Array;	
	var pstatus = new Object({'name':null,'status':null,'with':null});
	var pstatus2 = new Object({'name':null,'status':null,'with':null});
	if(!req.session.name)
	res.redirect('/');
	else{
		Player.findOne({name: (req.params.name).toUpperCase()}, function(error, player) {
	 		if(player.battling != null && player.battling != req.session.name)
	 			res.redirect('/players');
	 		else{
 			player.battling = req.session.name;
	  		var player = player;
	  				// pstatus = new Object({'name':req.session.name,
	  				// 					'status':'started',
	  				// 					'with':req.params.name});
			pstatus['name']=req.session.name;
  			pstatus['status']='started';
  			pstatus['with']=req.params.name;
  			plist.push( pstatus );

	  		player.save(function(err){
		  		Player.findOne({name: (req.session.name).toUpperCase()}, function(error, me) {
			  		me.battling = req.params.name;
			  		if(player.turn == 'your')
			  		me.turn = 'his';
			  		else
			  		me.turn = 'your';	
			  		req.session.battling = req.params.name;
			  		req.session.turn = me.turn;
			  		req.session.hp = me.hp;
		  			pstatus2['name']=req.params.name;
		  			pstatus2['status']='started';
		  			pstatus2['with']=req.session.name;
		  			plist.push( pstatus2 );

	 				io.emit('players', plist);	

			  		me.save(function(err){
			  			res.render("battle.ejs" , {player: player,session: req.session}) ;
			  		});
			  	});  			
	  		});	
 			}
 		});	
	}

});


app.post('/run', (req, res) => {
	var plist = new Array;	
	var pstatus = new Object({'name':null,'status':null,'with':null,'runned':null});
	var pstatus2 = new Object({'name':null,'status':null,'with':null,'runned':null});
	console.log(req.session.name+' runned from '+req.body.name);
	Player.findOne({name: req.body.name}, function(error, player) {
		player.battling = null;
  		var player = player;

  		pstatus['name']=req.session.name;
		pstatus['status']='ended';
		pstatus['with']=null;
		pstatus['runned']=req.session.name;
		plist.push( pstatus );

  		player.save(function(err){
	  		Player.findOne({name: (req.session.name).toUpperCase()}, function(error, me) {
		  		me.battling = null;
		  		me.turn = null;
		  		me.save(function(err){
		  			
		  			pstatus2['name']=req.body.name;
		  			pstatus2['status']='ended';
		  			pstatus2['with']=null;
		  			pstatus2['runned']=null;
		  			plist.push( pstatus2 );

		  			req.session.battling = null;
		  			req.session.turn = null;

					io.emit('players', plist);
					io.emit('battles', plist)

		  			res.redirect('/');
		  		});
		  	});  			
  		});
 	});
});


app.post('/attack', (req, res) => {
	var phitlist = new Array;
	var phit = new Object({'name': null, 'hit': 0, 'died': null});

	if(req.session.battling == (req.body.name).toUpperCase())
  	Player.findOne({name: (req.body.name).toUpperCase()}, function(error, player) {
  		attack = Math.floor(Math.random() * (req.session.level+2)) + req.session.level;
  		if( (player.hp-attack) >= 0 ){
  			player.hp = player.hp-attack;
  			var playerhp = player.hp;
  			var died = null;	
  		}

  		if((player.hp-attack) <= 0){
  			
  			var died = player.name;
  			var playerhp = 0;
	  		downlevel(player);
	  		levelup(req.session.name);
	  		player.turn = null;
	  		player.battling = null;
	  		req.session.turn = null;
	  		req.session.battling = null;

	  		player.save(function(err){

	  		Player.findOne({name: req.session.name},function(error,pl){
				pl.battling = null;
				pl.turn = null; 
				pl.save();
			});		
	  		
  			phit['name'] = player.name;
  			phit['hit'] = attack;
  			phit['died'] = player.name;	

  			phitlist.push(phit);
  			io.emit('battles', phitlist);
  			res.send(200, {hp:playerhp, attack, died } );
  			});
	  	}else
  		player.save(function(err){
  			phit['name'] = player.name;
  			phit['hit'] = attack;

  			phitlist.push(phit);
  			io.emit('battles', phitlist);
  			res.send(200, {hp:playerhp, attack, died } );
  		});
  		
  	});
  	else
  	res.send(200, 'false');

});


function downlevel(player){
	Player.findOne({name: player.name} ,function(error,pl){
		var pl = pl;
		pl.hp = pl.maxhp;
		if( (pl.level-1) == 0 )
			pl.level = 1;
		else
			pl.level = pl.level - 1;
		pl.save();
	});
}

function levelup(player){
	Player.findOne({name: player},function(error,pl){
		var pl = pl;
		pl.level = pl.level + 1;
		pl.maxhp = 50 + (pl.level*20);
		pl.hp = pl.maxhp;
		pl.save();
	});
}


app.post('/newplayer', (req, res) => {

	Player.findOne({name: req.body.name.toUpperCase()}, function(error, player) {
  		if(player == null){
  			new Player(req.body).save(function(err,pl){
  				console.log(pl.name+' logged in');
				res.redirect('/players');
			});
  		}
		else{
		console.log(player.name+' logged in');
		req.session.name = player.name;
		req.session.level = player.level;
		req.session.hp = player.hp;	
		req.session.maxhp = player.maxhp;
		req.session.battling = player.battling;
		req.session.turn = null;
		res.redirect('/players');	
		}
		
  		// res.render("battle.ejs" , {player: player}) ;
  	});
});




// app.get('/max', (req, res) => {

// 	Player.find(function(err, results) {
//     	results.forEach(function(pl){
//     		pl.update({exp: 0,expnext:50},function(err,pl){
//   			console.log('updated - '+pl.name);
//   			});
//     	})
//   	});
  		
// });
