const albumSchema = new Schema({
  title: String,
  description: String,
  shopId: String,
  photos: [{
    url: String,
    caption: String,
    createdAt: Date
  }],
  createdAt: Date
}); 