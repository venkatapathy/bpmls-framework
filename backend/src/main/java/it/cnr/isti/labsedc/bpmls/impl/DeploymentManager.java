package it.cnr.isti.labsedc.bpmls.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.core.io.Resource;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;

/**
 * This class is used to deploy various type of XML files such as Learning path
 * Learning Scenarios
 * 
 * @author venkat
 *
 */
public class DeploymentManager {

	/**
	 * This method is used to deploy Learning Scenarios (independent of its
	 * Learning Paths) it accepts the {@see Resource} from which the learning
	 * scenarios should be deployed and returns a {@see List} of
	 * {@see LearningScenario}
	 * 
	 * @param resources
	 * @return
	 * @throws IOException
	 */
	public List<LearningScenario> deployLearningScenarios(Resource[] resources) throws IOException {
		List<LearningScenario> deployedLearningScenarios = null;

		// try to unmarshall a xml file into a LearningPath jaxb class. If
		// successfull add them to
		// deployed LearningPaths
		for (Resource resource : resources) {
			try {

				JAXBContext jaxbContext = JAXBContext.newInstance(LearningScenario.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				LearningScenario learningScenario = (LearningScenario) jaxbUnmarshaller
						.unmarshal(resource.getInputStream());

				// if everything is fine
				if (learningScenario != null) {
					// if first time init list
					if (deployedLearningScenarios == null)
						deployedLearningScenarios = new ArrayList<LearningScenario>();
					deployedLearningScenarios.add(learningScenario);
				}

			} catch (JAXBException e) {
				// nothing to do, its not learning path compatable so just
				// ignore it
			}
		}
		return deployedLearningScenarios;
	}

	/**
	 * This method is used to deploy Learning Path (independent of its Learning
	 * Scenarios) it accepts the resource from which the learning path should be
	 * deployed and returns a list of {@see LearningPath}
	 * 
	 * @param resources
	 * @return
	 * @throws IOException
	 */
	public List<LearningPath> deployLearningPaths(Resource[] resources) throws IOException {
		List<LearningPath> deployedLearningPaths = null;

		// try to unmarshall a xml file into a LearningPath jaxb class. If
		// successfull add them to
		// deployed LearningPaths
		for (Resource resource : resources) {
			try {

				JAXBContext jaxbContext = JAXBContext.newInstance(LearningPath.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				LearningPath learningPath = (LearningPath) jaxbUnmarshaller.unmarshal(resource.getInputStream());

				// if everything is fine
				if (learningPath != null) {
					// if first time init list
					if (deployedLearningPaths == null)
						deployedLearningPaths = new ArrayList<LearningPath>();
					deployedLearningPaths.add(learningPath);
				}

			} catch (JAXBException e) {
				// nothing to do, its not learning path compatable so just
				// ignore it
			}
		}
		return deployedLearningPaths;
	}
}
