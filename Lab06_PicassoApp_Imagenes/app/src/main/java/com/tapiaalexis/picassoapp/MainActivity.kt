package com.tapiaalexis.picassoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ImagesAdapter(context: Context, images: List<String>, layout: Int) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    private val context: Context
    private val images: List<String>
    private val layout: Int
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(images[position]).fit()
            .placeholder(R.drawable.spinner)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.imageViewLayout) as ImageView
        }
    }

    init {
        this.context = context
        this.images = images
        this.layout = layout
    }

    fun getAnimalsLinks(): Array<String> {
        return arrayOf(
            "https://static.pexels.com/photos/86462/red-kite-bird-of-prey-milan-raptor-86462.jpeg",
            "https://static.pexels.com/photos/67508/pexels-photo-67508.jpeg",
            "https://static.pexels.com/photos/55814/leo-animal-savannah-lioness-55814.jpeg",
            "https://static.pexels.com/photos/40745/red-squirrel-rodent-nature-wildlife-40745.jpeg",
            "https://static.pexels.com/photos/33392/portrait-bird-nature-wild.jpg",
            "https://static.pexels.com/photos/62640/pexels-photo-62640.jpeg",
            "https://static.pexels.com/photos/38438/rattlesnake-toxic-snake-dangerous-38438.jpeg",
            "https://static.pexels.com/photos/33149/lemur-ring-tailed-lemur-primate-mammal.jpg",
            "https://static.pexels.com/photos/1137/wood-animal-dog-pet.jpg",
            "https://static.pexels.com/photos/40731/ladybug-drop-of-water-rain-leaf-40731.jpeg",
            "https://static.pexels.com/photos/40860/spider-macro-insect-arachnid-40860.jpeg",
            "https://static.pexels.com/photos/63282/crab-yellow-ocypode-quadrata-atlantic-ghostcrab-63282.jpeg",
            "https://static.pexels.com/photos/45246/green-tree-python-python-tree-python-green-45246.jpeg",
            "https://static.pexels.com/photos/39245/zebra-stripes-black-and-white-zoo-39245.jpeg",
            "https://static.pexels.com/photos/92000/pexels-photo-92000.jpeg",
            "https://static.pexels.com/photos/121445/pexels-photo-121445.jpeg",
            "https://static.pexels.com/photos/112603/pexels-photo-112603.jpeg",
            "https://static.pexels.com/photos/52961/antelope-nature-flowers-meadow-52961.jpeg",
            "https://static.pexels.com/photos/36450/flamingo-bird-pink-nature.jpg",
            "https://static.pexels.com/photos/20861/pexels-photo.jpg",
            "https://static.pexels.com/photos/54108/peacock-bird-spring-animal-54108.jpeg",
            "https://static.pexels.com/photos/24208/pexels-photo-24208.jpg"
        )
    }


    fun getPartyPics(): IntArray {
        return intArrayOf(
            R.drawable.ballons,
            R.drawable.christmas,
            R.drawable.concert,
            R.drawable.drinks,
            R.drawable.fiction,
            R.drawable.fire,
            R.drawable.glass,
            R.drawable.fireworks,
            R.drawable.glass,
            R.drawable.guy,
            R.drawable.notice,
            R.drawable.olives
        )
    }

    private fun checkForPermission() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ_EXTERNAL_MEMORY
            )
        }
    }

    private fun hasPermission(permissionToCheck: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(this, permissionToCheck)
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            PERMISSION_READ_EXTERNAL_MEMORY -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                images.clear()
                images.addAll(getImagesPath())
                imagesAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun getImagesPath(): Collection<String> {
        val listOfAllImages = ArrayList<String>()
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val columns = arrayOf(MediaStore.Images.Media._ID)
            val cursor: Cursor? = this.getContentResolver()
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    MediaStore.Images.Media._ID
                )
            if (cursor != null) {
                val idColumn: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id: Long = cursor.getLong(idColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    listOfAllImages.add(contentUri.toString())
                }
            }
        }
        return listOfAllImages
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        animals = getAnimalsLinks()!!
        parties = getPartyPics()!!
        images = getImagesPath() as MutableList<String>
        animalAdapter = AnimalAdapter(this, animals, R.layout.image_layout)
        partyAdapter = PartyAdapter(this, parties, R.layout.image_layout)
        imagesAdapter = ImagesAdapter(this, images, R.layout.image_layout)
        layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = animalAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_LinksUrl -> {
                binding.recyclerView.adapter = animalAdapter
                true
            } R.id.action_LinksRecursos -> {
                binding.recyclerView.adapter = partyAdapter
                true
            } R.id.action_LinksMemoriaExterna -> {
                checkForPermission()
                images.clear()
                images.addAll(getImagesPath())
                binding.recyclerView.adapter = imagesAdapter
                imagesAdapter!!.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}