package chargementDynamique;

import java.io.File;
import java.io.IOException;

import fr.miage.GUI.WrongPluginFrame;
import fr.miage.Model.Model;


public class RepositoryLoader
{
	static File base;

	static MyClassLoader mcl = new MyClassLoader();


	public void parcours(File base)
	{

		File[] listFiles = base.listFiles();
		for (int i = 0; i < listFiles.length; i++)
		{
			System.out.println("RepositoryLoader.parcours() " + listFiles[i].getName());
			if (listFiles[i].isDirectory())
			{
				parcours(listFiles[i]);
			} else if (listFiles[i].getName().endsWith(".class"))
			{
				try
				{

					String classPath = listFiles[i].getCanonicalPath();
					int index = classPath.indexOf("Plugins");
					String packageName = classPath.substring(index + 8);
					System.out.println("RepositoryLoader.parcours() packageName : " + packageName);
					if (Model.isWindows())
						packageName = packageName.replaceAll("\\\\", "\\.");
					if (Model.isUnix())
						packageName = packageName.replaceAll("/", "\\.");

					System.out.println("RepositoryLoader.parcours() packageName avec les points : " + packageName);

					int classIndex = packageName.indexOf(".class");
					if (classIndex > 0)
					{
						packageName = packageName.substring(0, classIndex);
						System.out.println("RepositoryLoader.parcours() packageName fini : " + packageName);
					}
					if (!(packageName.contains("IPluginAnalyse") || packageName.contains("IPluginView")))
					{
						mcl.path.add(listFiles[i].getParentFile());
						Class cl = mcl.loadClass(packageName);
//						System.out.println("RepositoryLoader.parcours() classe charg�e : " + cl.getName());
						Class[] interfaces = cl.getInterfaces();
						// System.out.println("SelectPluginFrame.SelectPluginFrame().new ActionListener() {...}.actionPerformed() "+
						// Model.getPlugin().getName());
						if (interfaces.length != 0)
						{
							for (int j = 0; j < interfaces.length; j++)
							{
//								System.out.println("SelectPluginFrame.SelectPluginFrame() " + cl.getInterfaces()[j]);
								if (interfaces[j].getName().contains("IPluginView"))
								{
									Model.addViewPlugin(cl);
									System.out.println("Plugin de vue ajout� : "+cl.getName());
								} else if (interfaces[j].getName().contains("IPluginAnalyse"))
								{
									Model.addAnalysisPlugin(cl);
									System.out.println("Plugin d'analyse ajout� "+cl.getName());
								}
							}
						} else
							System.out.println("RepositoryLoader.parcours() interface, on ne la charge pas");
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public static void main(String[] args)
	{
		RepositoryLoader repo = new RepositoryLoader();
		repo.base = new File("C:\\Users\\deptinfo\\Documents\\Plugins");
		repo.parcours(base);

	}

}
