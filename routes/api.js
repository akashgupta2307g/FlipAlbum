router.post('/api/albums', async (ctx) => {
  const { title, description } = ctx.request.body;
  const { shop } = ctx.session;

  const album = await Album.create({
    title,
    description,
    shopId: shop
  });

  ctx.body = album;
}); 